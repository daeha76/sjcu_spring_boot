package org.zerock.board.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.zerock.board.dto.BoardDTO;
import org.zerock.board.entity.Board;
import org.zerock.board.repository.BoardRepository;
import org.zerock.board.repository.MemberRepository;
import org.zerock.board.entity.Member;

@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService{

    private final BoardRepository repository;
    private final MemberRepository memberRepository;

    @Override
    public List<BoardDTO> getList() {
        List<Board> result = repository.findAll();

        return result.stream()
                     .map(board -> entityToDTO(board, board.getWriter(), 0L))
                     .collect(Collectors.toList());
    }
    
    @Override
    public BoardDTO get(Long bno) {
        Board board = repository.findById(bno)
                                .orElseThrow(() -> new RuntimeException("해당 게시글이 없습니다."));
        return entityToDTO(board, board.getWriter(), 0L);
    }
    
    @Override
    public Long register(BoardDTO dto) {

        log.info(dto);

        Member member = memberRepository.findById(dto.getWriterEmail())
                                        .orElseGet(() -> {
                                            // 존재하지 않으면 새로운 Member를 생성 및 저장
                                            Member newMember = Member.builder()
                                                                    .email(dto.getWriterEmail())
                                                                    .name(dto.getWriterName()) // DTO에서 이름을 가져오는 경우
                                                                    .pwd("defaultPwd") // 기본 비밀번호 설정 또는 처리
                                                                    .build();
                                            return memberRepository.save(newMember);
                                        });
        Board board  = dtoToEntity(dto);
        board.setWriter(member);

        repository.save(board);

        return board.getBno();
    }

    @Override
    public void modify(BoardDTO dto) {
        Board board = repository.findById(dto.getBno())
                                .orElseThrow(() -> new RuntimeException("해당 게시글이 없습니다."));
        board.changeTitle(dto.getTitle());
        board.changeContent(dto.getContent());
        
        repository.save(board);
    }

    @Override
    public void removeWithReplies(Long bno) {
        repository.deleteById(bno);
    }

  
   

	
};
