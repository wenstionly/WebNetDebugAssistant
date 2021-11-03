package cc.lj1.tools.webnetdebugassistant.controller;

import cc.lj1.tools.webnetdebugassistant.tools.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/id/generator")
public class IdGeneratorController {
    @Autowired
    IdWorker idWorker;

    @GetMapping("")
    public String getId() {
        return String.valueOf(idWorker.nextId());
    }
}
