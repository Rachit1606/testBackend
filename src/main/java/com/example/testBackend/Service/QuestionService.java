package com.example.testBackend.Service;

import com.example.testBackend.Entities.LaunchTestRequest;
import com.example.testBackend.Entities.Question;
import com.example.testBackend.Repository.QuestionRepository;
import com.example.testBackend.Service.Impl.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SampleOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService implements IQuestionService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public List<Question> getAllQuestionsByUserId(String userId) {
        return questionRepository.findAllByUserId(userId);
    }
    @Override
    public Optional<Question> getQuestionById(String id) {
        return questionRepository.findById(id);
    }
    @Override
    public Question addQuestion(Question question) {
        return questionRepository.save(question);
    }
    @Override
    public Question updateQuestion(String id, Question updatedQuestion) {
        updatedQuestion.setQuestionId(id);
        return questionRepository.save(updatedQuestion);
    }
    @Override
    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

    @Override
    public List<Question> getRandomQuestions(LaunchTestRequest request) {
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("courseId").is(request.getCourseId())
                        .and("chapterId").in(request.getChapterIds())
                        .and("subchapterId").in(request.getSubchapterIds())
                        .and("difficulty").lte(request.getDifficultyLevel())
        );

        SampleOperation sampleOperation = Aggregation.sample(request.getNumberOfQuestions());

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sampleOperation);

        AggregationResults<Question> aggregationResults = mongoTemplate.aggregate(aggregation, "questions", Question.class);

        return aggregationResults.getMappedResults();
    }
    @Override
    public List<Question> getAllQuestionsByUserAndCourse(String userId, String courseId) {
        return questionRepository.findByUserIdAndCourseId(userId, courseId);
    }
}
