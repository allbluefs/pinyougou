package com.pinyougou.user.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
import entity.Result;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout = 50000)
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String smsCode){
		try {
			Boolean result=userService.checkSmsCode(smsCode,user.getPhone());
			if (!result) {
				return new Result(false, "验证码无效");
			}
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	@RequestMapping("/sendSmsCode")
	public Result sendSmsCode(String phone){
		try {
			userService.sendSmsCode(phone);
			return new Result(true,"发送成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false,e.getMessage());
		} catch (Exception e) {
			return new Result(false,"发送失败");
		}
	}


	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}

	@RequestMapping("/getLoginName")
	public Map<String,String> getLoginName() {
		//基于安全框架springsecurity获取登录人用户名
		String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, String> map = new HashMap<>();
		map.put("loginName", loginName);
		return map;
	}


	
}
