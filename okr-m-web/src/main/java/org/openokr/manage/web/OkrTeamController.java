package org.openokr.manage.web;

import com.zzheng.framework.adapter.vo.ResponseResult;
import com.zzheng.framework.base.utils.JSONUtils;
import org.openokr.application.framework.annotation.JsonPathParam;
import org.openokr.application.web.BaseController;
import org.openokr.manage.service.IOkrTeamService;
import org.openokr.manage.vo.TeamsExtVO;
import org.openokr.sys.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * OKR团队管理
 *
 * @author hjh
 */
@Controller
@RequestMapping("/manage/okrTeam")
public class OkrTeamController extends BaseController {

    @Autowired
    private IOkrTeamService okrTeamService;

    @GetMapping(value = "/init.htm")
    public String init() {
        return "manage/okrTeamList";
    }

    @GetMapping(value = "/teamForm.htm")
    public String editTeam1(String id, Model model) {
        TeamsExtVO teamsExtVO = okrTeamService.getByTeamId(id);
        model.addAttribute("teamsExtVO", teamsExtVO);
        return "manage/okrTeamForm";
    }

    /**
     * 获取用户关联的团队(影响团队或者参与的团队)
     */
    @RequestMapping(value = "/getTeamList.json")
    @ResponseBody
    public List<TeamsExtVO> getTeamList() {
        TeamsExtVO teamsVO = new TeamsExtVO();
        teamsVO.setUserId(getCurrentUserId());
        return okrTeamService.getRelTeams(teamsVO);
    }

    /**
     * 我创建的团队列表(排除公司团队)
     */
    @RequestMapping(value = "/getCreatedTeam.json")
    @ResponseBody
    public List<TeamsExtVO> getCreatedTeam() {
        return okrTeamService.createdTeam(getCurrentUserId());
    }

    /**
     * 获取公司团队:目前只有一个公司团队,且不允许新增,但可以新增和删除人员
     */
    @RequestMapping(value = "/getCompanyTeam.json")
    @ResponseBody
    public List<TeamsExtVO> getCompanyTeam() {
        TeamsExtVO teamsVO = new TeamsExtVO();
        teamsVO.setType("1");//类型为公司团队
        return okrTeamService.getRelTeams(teamsVO);
    }

    /**
     * 保存团队
     * @return
     */
    @RequestMapping(value = "/saveTeams.json")
    @ResponseBody
    public ResponseResult saveTeams(@JsonPathParam("$.teamExtVO") TeamsExtVO teamsVO) {
        teamsVO.setUserId(getCurrentUserId());
        return okrTeamService.saveTeams(teamsVO);
    }

    /**
     * 删除团队成员
     * @return
     */
    @RequestMapping(value = "/deleteTeamUser.json", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult deleteTeamUser(@JsonPathParam("$.teamId") String teamId,
                                         @JsonPathParam("$.userIdList") Object userIdList) {
        List<String> userIds = JSONUtils.objectToList(userIdList, String.class);
        return okrTeamService.deleteTeamUser(teamId, userIds);
    }

    /**
     * 解散团队
     * @return
     */
    @RequestMapping(value = "/disbandTeam.json")
    @ResponseBody
    public ResponseResult disbandTeam(String teamId) {
        return okrTeamService.disbandTeam(teamId);
    }

    @GetMapping(value = "/getUsersByTeamId.json")
    @ResponseBody
    public List<UserVO> getUsersByTeamId(String teamId) {
        return okrTeamService.getUsersByTeamId(teamId);
    }
}