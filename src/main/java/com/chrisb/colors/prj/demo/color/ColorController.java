package com.chrisb.colors.prj.demo.color;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colors")
public class ColorController {

    private final ColorService colorService;

    @Autowired
    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @ApiOperation(value = "Get Colors Count", notes = "Returns colors count for the last 1 hour or for all-time if the related 'data' request parameter is been provided with 'allTime' value")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved")
    })
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ColorCountResource getColorsCount(@RequestParam(value = "data", required = false)
                                                 @ApiParam(name = "data", value = "If provided with 'allTime' value then all-time colors count will be returned. Optional")
                                                 String data) {
        return colorService.getColorCounts(data);
    }

}
