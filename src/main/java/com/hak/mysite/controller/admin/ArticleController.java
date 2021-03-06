package com.hak.mysite.controller.admin;

import com.github.pagehelper.PageInfo;
import com.hak.base.RestDto;
import com.hak.base.controller.BaseController;
import com.hak.mysite.model.TContents;
import com.hak.mysite.model.vo.TContentsVo;
import com.hak.mysite.service.ArticleService;
import com.hak.mysite.service.IndexService;
import com.hak.mysite.service.MetaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author hak
 * @description 后台首页
 * @date 2020/8/8
 */
@Api("后台首页")
@Controller
@RequestMapping(value = "/admin/article")
public class ArticleController extends BaseController {

    @Autowired
    IndexService indexService;

    @Autowired
    ArticleService articleService;

    @Autowired
    MetaService metaService;

    @ApiOperation("文章列表")
    @GetMapping(value = {"/list"})
    @ResponseBody
    public RestDto list(final Pageable pageable) throws Exception{

        PageInfo<TContentsVo> list = articleService.getTContentsList(pageable,super.getLoginInfo());

        return RestDto.layUISuccess(list);
    }

    @ApiOperation("文章预览")
    @GetMapping("single/{cid}")
    public String  single(@PathVariable int cid, HttpServletRequest request) throws Exception{
        TContents tContents = articleService.getContentsById(cid);
        request.setAttribute("tContents", tContents);
        return "admin/single";
    }

    @ApiOperation("文章删除")
    @GetMapping("del/{cid}")
    @ResponseBody
    public RestDto delete(@PathVariable int cid) throws Exception{

        int i = articleService.deleteTContentsById(cid);

        return RestDto.success();
    }
    @ApiOperation("编辑文章跳转")
    @GetMapping("/edit/{cid}")
    public String  edit(@PathVariable int cid, HttpServletRequest request) throws Exception{

        TContents tContents = articleService.getContentsById(cid);
        List list = metaService.getCategorieList();
        request.setAttribute("tContents", tContents);
        request.setAttribute("tCategories", list);
        return "admin/article_edit";
    }
    @ApiOperation("发布文章/保存草稿/编辑保存")
    @PostMapping(value = {"/publish/{type}"})
    @ResponseBody
    public RestDto publish(@RequestParam(name = "cId", required = false)Integer cId,
                          @RequestParam(name = "title", required = false)String title,
                          @RequestParam(name = "titlePic", required = false)String titlePic,
                          @RequestParam(name = "tags", required = false)String tages,
                          @RequestParam(name = "categories", required = false)Integer categories,
                          @RequestParam(name = "content", required = false)String content,
                          @RequestParam(name = "allowOpen", required = false)String allowOpen,
                          @PathVariable String type) throws Exception{
        int allowComment = allowOpen=="on"?1:0;
        TContents tContents = new TContents();
        tContents.setcId(cId);
        tContents.setTitle(title);
        tContents.setTitlePic(titlePic);
        tContents.setCreated(new Date());
        tContents.setAuthorId(super.getLoginInfo().getUId());
        tContents.setTags(tages);
        tContents.setCategories(categories);
        tContents.setAllowComment(allowComment);
        tContents.setContent(content);

        if ("fb".equals(type)){
            //发布
            tContents.setStatus(1);
            articleService.insertTContents(tContents);
        }else if ("cg".equals(type)){
            //草稿
            tContents.setStatus(0);
            articleService.insertTContents(tContents);
        }else if ("xgfb".equals(type)){
            //编辑发布
            tContents.setStatus(1);
            articleService.updateTContents(tContents);
        }else if ("xgcg".equals(type)){
            //编辑草稿
            tContents.setStatus(0);
            articleService.updateTContents(tContents);
        }
        return RestDto.success();
    }



}
