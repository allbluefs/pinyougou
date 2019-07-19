import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.solrutil.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date:2019/7/14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext*.xml")
public class TestDemo {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private SolrUtil solrUtil;


    @Test
    public void testAdd(){
        TbItem item=new TbItem();
        item.setId(1L);
        item.setTitle("华为6plus");
        item.setCategory("手机");
        item.setBrand("华为");
        item.setGoodsId(1L);
        item.setPrice(new BigDecimal(30000.00));
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    @Test
    public void findById(){
        TbItem item = solrTemplate.getById(1L, TbItem.class);
        System.out.println(item.getTitle());
    }
    @Test
    public void deleteById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    @Test
    public void testAddList(){
        List<TbItem> list=new ArrayList<>();
        for (Long i = 1L; i <100 ; i++) {
            TbItem item=new TbItem();
            item.setId(i);
            item.setTitle("华为6plus"+i);
            item.setCategory("手机");
            item.setBrand("华为");
            item.setGoodsId(i);
            item.setPrice(new BigDecimal(30000.00));
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Test
    public void testPageQuery(){
        Query query=new SimpleQuery("*:*");
        //开始索引
        query.setOffset(1);
        query.setRows(5);
        ScoredPage<TbItem> page=solrTemplate.queryForPage(query,TbItem.class);
        List<TbItem> content=page.getContent();
        System.out.println("content+"+content);
        for (TbItem item : content) {
            System.out.println(item.getTitle());
        }
        System.out.println("总记录数："+page.getTotalElements());
        System.out.println("总页数："+page.getTotalPages());
    }
    @Test
    public void testPageQueryWhere(){
        Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_title");
        criteria=criteria.contains("3");
        //开始索引
        query.setOffset(0);
        query.setRows(10);
        query.addCriteria(criteria);
        ScoredPage<TbItem> page=solrTemplate.queryForPage(query,TbItem.class);
        List<TbItem> content=page.getContent();
        for (TbItem item : content) {
            System.out.println(item.getTitle());
        }
        System.out.println("总记录数："+page.getTotalElements());
        System.out.println("总页数："+page.getTotalPages());
    }
    //删除全部数据
    @Test
    public void deleteAll(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    @Test
    public void importData(){
        solrUtil.dataImport();
    }


}
