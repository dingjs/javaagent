package com.wenshuo.agent.analyzer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @date 2024/09/06 15:00
 **/
@Controller
public class IndexController {

    // @RequestMapping({"/"})
    public String index() {
        return "jsp/prc";
    }
}
