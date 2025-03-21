package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.InterestDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.InterestMapper;
import com.tihonya.datingapp.model.Interest;
import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.repository.InterestRepository;
import com.tihonya.datingapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class InterestService {
    private static final String INTEREST_NOT_FOUND = "Interest not found";

    private final InterestRepository interestRepository;
    private final InterestMapper interestMapper;
    private final UserRepository userRepository;

    public InterestService(InterestRepository interestRepository,
                           InterestMapper interestMapper, UserRepository userRepository) {
        this.interestRepository = interestRepository;
        this.interestMapper = interestMapper;
        this.userRepository = userRepository;
    }

    // Создание интереса
    public InterestDto createInterest(InterestDto interestDto) {
        Interest interest = new Interest();
        interest.setName(interestDto.getName());
        interestRepository.save(interest);
        return interestMapper.toDto(interest);
    }

    // Получение всех интересов
    public List<InterestDto> getAllInterests() {
        return interestRepository.findAll().stream()
                .map(interestMapper::toDto)
                .toList();
    }

    // Получение интереса по ID
    public InterestDto getInterestById(Long id) {
        Interest interest = interestRepository.findById(id).orElseThrow(()
                -> new NotFoundException(INTEREST_NOT_FOUND));
        return interestMapper.toDto(interest);
    }

    // Обновление интереса
    public InterestDto updateInterest(Long id, InterestDto interestDto) {
        Interest interest = interestRepository.findById(id).orElseThrow(()
                -> new NotFoundException(INTEREST_NOT_FOUND));
        interest.setName(interestDto.getName());
        interestRepository.save(interest);
        return interestMapper.toDto(interest);
    }

    // Удаление интереса
    public void deleteInterest(Long id) {
        Interest interest = interestRepository.findById(id).orElseThrow(()
                -> new NotFoundException(INTEREST_NOT_FOUND));
        // Убираем связь между интересом и пользователями
        for (User user : interest.getUsers()) {
            user.getInterests().remove(interest);
        }
        userRepository.saveAll(interest.getUsers()); // Сохраняем изменения у пользователей
        interestRepository.delete(interest);
    }
}


