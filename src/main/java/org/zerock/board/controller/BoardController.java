package org.zerock.board.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.board.dto.BoardDTO;
import org.zerock.board.entity.Board;
import org.zerock.board.service.BoardService;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

@Controller
@RequestMapping("/board/")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

   @GetMapping("/list")
   public void list(Model model){
        log.info("list..........");

        List<BoardDTO> result = boardService.getList();
        model.addAttribute("result", result);
   }

    @GetMapping("/register")
    public void register(){
        log.info("regiser get...");
    }

    @PostMapping("/register")
    public String registerPost(BoardDTO dto, RedirectAttributes redirectAttributes){

        log.info("dto..." + dto);
        //새로 추가된 엔티티의 번호
        Long bno = boardService.register(dto);

        log.info("BNO: " + bno);

        redirectAttributes.addFlashAttribute("msg", bno);

        return "redirect:/board/list";
    }

    @GetMapping("/modify")
    public String modify(@RequestParam("bno") Long bno, Model model) {
        log.info("modify get... with bno: " + bno);

        BoardDTO dto = boardService.get(bno);
        if (dto == null) {
            log.error("No board found for bno: " + bno);
            return "redirect:/board/list";
        }

        model.addAttribute("dto", dto);
        model.addAttribute("page", 0);
        model.addAttribute("type", "");
        model.addAttribute("keyword", "");

        return "board/modify";
    }

    @PostMapping("/modify")
    public String modify(BoardDTO dto, @ModelAttribute("page") int page, RedirectAttributes redirectAttributes){

        log.info("dto: " + dto);

        boardService.modify(dto);

        redirectAttributes.addAttribute("bno", dto.getBno());
        redirectAttributes.addAttribute("title", dto.getTitle());
        redirectAttributes.addAttribute("content", dto.getContent());

        return "redirect:/board/list";
    }
    
    @PostMapping("/remove")
    public String remove(@RequestParam("bno") Long bno, RedirectAttributes redirectAttributes) {
        log.info("remove... bno: " + bno);
        
        boardService.removeWithReplies(bno);
        redirectAttributes.addFlashAttribute("msg", bno);
        return "redirect:/board/list";
    }
}
