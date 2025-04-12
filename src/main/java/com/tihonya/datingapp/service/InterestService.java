package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.InterestDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.InterestMapper;
import com.tihonya.datingapp.model.Interest;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.InterestRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class InterestService {
    private static final String INTEREST_NOT_FOUND = "Interest not found";
    private static final String CACHE_KEY_INTERESTS = "all_interests";

    private final InterestRepository interestRepository;
    private final InterestMapper interestMapper;
    private final CacheService cacheService;
    private final ProfileRepository profileRepository;

    public InterestService(InterestRepository interestRepository,
                           InterestMapper interestMapper,
                           CacheService cacheService, ProfileRepository profileRepository) {
        this.interestRepository = interestRepository;
        this.interestMapper = interestMapper;
        this.cacheService = cacheService;
        this.profileRepository = profileRepository;
    }

    // Создание интереса
    public InterestDto createInterest(InterestDto interestDto) {
        Interest interest = new Interest();
        interest.setName(interestDto.getName());
        interestRepository.save(interest);
        clearInterestCache();
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
        String cacheKey = "interest_" + id;
        InterestDto cachedInterest = cacheService.getFromCache(cacheKey, InterestDto.class);
        if (cachedInterest != null) {
            return cachedInterest;
        }

        Interest interest = interestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(INTEREST_NOT_FOUND));
        InterestDto interestDto = interestMapper.toDto(interest);
        cacheService.saveToCache(cacheKey, interestDto);
        return interestDto;
    }

    public void clearInterestCache() {
        cacheService.clearCache(CACHE_KEY_INTERESTS);
    }

    // Обновление интереса
    public InterestDto updateInterest(Long id, InterestDto interestDto) {
        Interest interest = interestRepository.findById(id).orElseThrow(()
                -> new NotFoundException(INTEREST_NOT_FOUND));
        interest.setName(interestDto.getName());
        interestRepository.save(interest);
        clearInterestCache();
        return interestMapper.toDto(interest);
    }

    // Удаление интереса
    public void deleteInterest(Long id) {
        Interest interest = interestRepository.findById(id).orElseThrow(()
                -> new NotFoundException(INTEREST_NOT_FOUND));
        // Убираем связь между интересом и пользователями
        for (Profile profile : interest.getProfiles()) {
            profile.getInterests().remove(interest);
        }
        profileRepository.saveAll(interest.getProfiles()); // Сохраняем изменения у пользователей
        interestRepository.delete(interest);
        clearInterestCache();
    }

    @Transactional
    public List<InterestDto> createInterests(List<InterestDto> interestDtos) {
        List<Interest> interests = interestDtos.stream()
                .map(dto -> {
                    Interest interest = new Interest();
                    interest.setName(dto.getName());
                    return interest;
                })
                .collect(Collectors.toList());

        // Сохраняем все интересы сразу
        interestRepository.saveAll(interests);

        // Возвращаем созданные интересы
        return interests.stream()
                .map(interestMapper::toDto)
                .collect(Collectors.toList());
    }
}


