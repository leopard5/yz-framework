package com.yz.framework.zookeeper.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.zookeeper.data.ACL;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yz.framework.zookeeper.TreeNode;

@RequestMapping("zoo")
@Controller
public class ZookeeperController {

	@Resource
	private ZookeeperManager zookeeperManager;

	@RequestMapping("index")
	public String index() {
		return "zookeeper/index";
	}

	@RequestMapping("tree")
	@ResponseBody
	public String getTree() {
		TreeNode node = zookeeperManager.getTree("/");
		String json = JSON.toJSONString(node.getChildren());
		return json;
	}

	@RequestMapping(value = "getData", method = RequestMethod.GET)
	@ResponseBody
	public String getNode(String path) {
		return zookeeperManager.getNodeData(path);
	}

	@RequestMapping(value = "delete", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String delete(String fullPath) {
		return zookeeperManager.delete(fullPath);
	}

	@RequestMapping(value = "setNodeData", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String setNodeData(String fullPath, String data) {

		return zookeeperManager.setNodeData(fullPath, data);
	}

	@RequestMapping(value = "addNode", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String addNode(String parentFullPath, String path) {
		String[] paths = path.split("\\.");
		TreeNode treeNode = zookeeperManager.addNode(parentFullPath, paths[0]);
		if (paths.length > 1) {
			TreeNode subNode = null;
			for (int i = 1; i < paths.length; i++) {

				String subPath = paths[i];
				if (!StringUtils.hasText(subPath)) {
					continue;
				}
				if (subNode == null) {
					subNode = zookeeperManager.addNode(treeNode.getFullPath(), subPath);
					if (treeNode.getChildren() == null) {
						treeNode.setChildren(new ArrayList<TreeNode>());
					}
					treeNode.getChildren().add(subNode);
				} else {
					TreeNode node = zookeeperManager.addNode(subNode.getFullPath(), subPath);
					if (subNode.getChildren() == null) {
						subNode.setChildren(new ArrayList<TreeNode>());
					}
					subNode.getChildren().add(node);
					subNode = node;
				}
			}
		}
		return JSON.toJSONString(treeNode);
	}

	@RequestMapping(value = "getACL", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getACL(String path) {

		List<ACL> list = zookeeperManager.getACL(path);
		return JSON.toJSONString(list);
	}
}
