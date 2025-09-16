package com.example.sbb.question.repository;

import com.example.sbb.question.entity.Question;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionRepositoryTest {

    @Autowired
    QuestionRepository questionRepository;

    @Test
   public void testSave(){
       Question q1 = new Question();
       q1.setSubject("sbb가 뭔가요?");
       q1.setContent("sbb는 질의 응답 게시판 인가요?");
       System.out.println("q1 : " + q1);
       Question q2 = questionRepository.save(q1);

       Question q3 = Question.builder().
               content("질문 내용")
               .subject("질문해주세요")
               .build();
       Question q4 = questionRepository.save(q3);
       assertEquals(2, q4.getId());

   }

   @Test
   public void testFindAll(){
       List<Question> questionList = questionRepository.findAll();
   }

   @Test
    public void testFindEntity(){
        Question q1 = questionRepository.findBySubjectLike("%sbb%")
                .orElseThrow(EntityNotFoundException::new);
       assertEquals("sbb가 뭔가요?", q1.getSubject());
//       Question q1 = questionRepository.findById(1L)
//               .orElseThrow(() -> new EntityNotFoundException("해당 엔티티가 존재하지 않음"));
   }

}