package com.tqz.business.rest;

import com.tqz.business.model.domain.Offer;
import com.tqz.business.model.request.AddOfferRequest;
import com.tqz.business.model.request.FindOfferByProductRequestRequest;
import com.tqz.business.service.OfferService;
import com.tqz.business.service.ProductService;
import com.tqz.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RequestMapping("/rest/offer")
@Controller
public class OfferRestApi {
    private static Logger logger = Logger.getLogger(OfferRestApi.class.getName());

    @Autowired
    private OfferService offerService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public Model addOffer(@RequestBody Offer offer, Model model) {
        model.addAttribute("success", offerService.addOffer(new AddOfferRequest(offer.getOwnerId(), offer.getProductId(),
                offer.getPrice(), offer.getCapacity())));
        return model;
    }

    @RequestMapping(value = "/product", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getOfferByProductId(@RequestParam("productId") int productId, @RequestParam("num") int num, Model model) {
        List<Offer> offerList = offerService.findOfferByProductId(new FindOfferByProductRequestRequest(num, productId));
        if (null == offerList) {
            model.addAttribute("success", false);
        } else {
            model.addAttribute("success", true);
            model.addAttribute("offers", offerList);
        }
        return model;
    }

}
