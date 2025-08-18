package com.matcheval.stage.controller;

import com.matcheval.stage.interfaces.IstatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("manager")

public class StatMangController {
    @Autowired
    IstatService statService;
}
