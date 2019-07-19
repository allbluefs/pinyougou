package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import groupEntity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        goods.getGoods().setAuditStatus("0");//未审核
        TbGoods tbGoods = goods.getGoods();
        goodsMapper.insert(tbGoods);
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(goodsDesc);

        //是否启用规格
        if (goods.getGoods().getIsEnableSpec().equals("1")) {
            List<TbItem> itemList = goods.getItemList();
            for (TbItem item : itemList) {
//		`title` varchar(100) NOT NULL COMMENT '商品标题',   // 商品名称（SPU名称）+ 商品规格选项名称 中间以空格隔开
                String title = tbGoods.getGoodsName();
                //当前商品特有规格数据  "spec":{"网络":"移动3G","机身内存":"64G"}
                String spec = item.getSpec();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                for (String key : map.keySet()) {
                    String specOptionName = map.get(key);
                    title += " " + specOptionName;
                }
                item.setTitle(title);
                //抽取封装代码
                setItemValue(tbGoods, goodsDesc, item);

                //保存item
                itemMapper.insert(item);
            }
        }else {
            //不启用规格
            TbItem item = new TbItem();
            item.setTitle(tbGoods.getGoodsName());
//			`spec` varchar(200) DEFAULT NULL,
            item.setSpec("{}");
//			 `price` decimal(20,2) NOT NULL COMMENT '商品价格，单位为：元',
            item.setPrice(tbGoods.getPrice());
//			 `num` int(10) NOT NULL COMMENT '库存数量',
            item.setNum(9999);
//			 `status` varchar(1) NOT NULL COMMENT '商品状态，1-正常，2-下架，3-删除',
            item.setStatus("1");
//			 `is_default` varchar(1) DEFAULT NULL,
            item.setIsDefault("1");
            //抽取封装item数据
            setItemValue(tbGoods, goodsDesc, item);
            itemMapper.insert(item);
        }


    }

    private void setItemValue(TbGoods tbGoods, TbGoodsDesc goodsDesc, TbItem item) {
        //		`image` varchar(2000) DEFAULT NULL COMMENT '商品图片',  // 从 tb_goods_desc item_images中获取第一张
        //[{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVnGbYuAO6AHAAjlKdWCzvg253.jpg"},
        // {"color":"蓝色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVnKX4yAbCC0AAFa4hmtWek406.jpg"},
        // {"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVnKX5WAOsqXAAETwD7A1Is409.jpg"}]
        String itemImages = goodsDesc.getItemImages();
        List<Map> imageList = JSON.parseArray(itemImages, Map.class);
        if (imageList != null && imageList.size() > 0) {
            String image = (String) imageList.get(0).get("url");
            item.setImage(image);
        }
//		`categoryId` bigint(10) NOT NULL COMMENT '所属类目，叶子类目',  //三级分类id
        item.setCategoryid(tbGoods.getCategory3Id());
//		`create_time` datetime NOT NULL COMMENT '创建时间',
        item.setCreateTime(new Date());
//		`update_time` datetime NOT NULL COMMENT '更新时间',
        item.setUpdateTime(new Date());
//		`goods_id` bigint(20) DEFAULT NULL,
        item.setGoodsId(tbGoods.getId());
//		`seller_id` varchar(30) DEFAULT NULL,
        item.setSellerId(tbGoods.getSellerId());
//		//以下字段作用：方便商品搜索
//		`category` varchar(200) DEFAULT NULL, //三级分类名称
        String categoryName = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
        item.setCategory(categoryName);
//		`brand` varchar(100) DEFAULT NULL,//品牌名称
        String brandName = brandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName();
        item.setBrand(brandName);
//		`seller` varchar(200) DEFAULT NULL,//商家店铺名称
        String nickName = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId()).getNickName();
        item.setSeller(nickName);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination addItemSolrDestination;
    @Autowired
    private Destination deleItemSolrDestination;
    @Autowired
    private Destination addItemPageDestination;
    @Autowired
    private Destination deleItemPageDestination;


    //上下架
    @Override
    public void updatesIsMarketable(Long[] ids, String isMarketable) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            if (goods.getAuditStatus().equals("1")) {

                //商品上架
                if ("1".equals(isMarketable)) {
                    //更新索引库
                    jmsTemplate.send(addItemSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id+"");
                        }
                    });

                    //生成静态页
                    jmsTemplate.send(addItemPageDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id+"");
                        }
                    });

                }
                //商品下架
                if ("0".equals(isMarketable)) {
                    jmsTemplate.send(deleItemSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id+"");
                        }
                    });
                    //删除静态页
                    jmsTemplate.send(deleItemPageDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(id+"");
                        }
                    });

                }

                goods.setIsMarketable(isMarketable);
                goodsMapper.updateByPrimaryKey(goods);
            }else {
                throw new RuntimeException("上架的商品必须是已经审核的商品");
            }
        }
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

}
