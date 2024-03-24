package com.example.testBackend.Controller;

import com.example.testBackend.Entities.*;
import com.example.testBackend.Repository.AttemptRepository;
import com.example.testBackend.Service.Impl.IAttemptService;
import com.example.testBackend.Service.Impl.IQuestionService;
import com.example.testBackend.Service.Impl.ITestService;
import com.example.testBackend.Service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AttemptController {
    @Autowired
    private ITestService testService;

    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private IQuestionService questionService;
    @Autowired
    private IAttemptService attemptService;

    @PostMapping("/launch-test")
    public ResponseEntity<?> launchTest(@RequestBody LaunchTestRequest request) {
        List<Question> selectedQuestions = questionService.getRandomQuestions(request);
        List<String> questionIds = new ArrayList<>();
        for (Question sq: selectedQuestions) {
            String qid = sq.getQuestionId();
            questionIds.add(qid);
        }
        // Create AttemptedQuestion objects with questionIds
        List<AttemptedQuestion> attemptedQuestions = new ArrayList<>();
        for (String questionId : questionIds) {
            AttemptedQuestion attemptedQuestion = new AttemptedQuestion();
            attemptedQuestion.setQuestionId(questionId);
            attemptedQuestions.add(attemptedQuestion);
        }

        Attempt attempt = attemptService.createAttempt(request.getUserId(), request.getTestId(), attemptedQuestions);

        attempt.setAttemptedQuestions(attemptedQuestions);
        return ResponseEntity.ok(selectedQuestions);
    }


    @PostMapping("/finish-test")
    public ResponseEntity<?> finishTest(@RequestBody FinishTestRequest request) {
        Attempt attempt = attemptRepository.findById(request.getAttemptId()).orElse(null);
        List<AttemptedQuestion> attemptedQuestions = request.getAttemptedQuestions();
        attempt.setAttemptedQuestions(attemptedQuestions);
        attemptService.updateAttemptAndSaveResult(attempt);
        return ResponseEntity.ok("Test attempt updated successfully");
    }

}
