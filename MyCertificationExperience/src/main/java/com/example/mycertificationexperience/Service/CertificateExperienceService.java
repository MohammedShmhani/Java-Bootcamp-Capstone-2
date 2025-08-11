package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Api.ApiException;
import com.example.mycertificationexperience.Model.CertificationExperience;
import com.example.mycertificationexperience.Model.Contributor;
import com.example.mycertificationexperience.Repository.CategoryRepository;
import com.example.mycertificationexperience.Repository.CertificationExperienceRepository;
import com.example.mycertificationexperience.Repository.ContributorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateExperienceService {

    private final CertificationExperienceRepository experienceRepository;
    private final ContributorRepository contributorRepository;
    private final CategoryRepository categoryRepository; // اختياري لكن مفيد للتحقق

    // READ
    public List<CertificationExperience> getAll() {
        return experienceRepository.findAll();
    }

    public CertificationExperience getOne(Integer id) {
        CertificationExperience exp = experienceRepository.findCertificationExperienceById(id);
        if (exp == null) throw new ApiException("Experience not found");
        return exp;
    }

    @Transactional
    // CREATE — فقط CONTRIBUTOR ويُثبَّت المالك = contributorId
    public void add(Integer contributorId, CertificationExperience body) {
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");

        // تحقّق وجود التصنيف (اختياري لكنه أفضل)
        if (body.getCategoryId() == null ||
                categoryRepository.findCategoryById(body.getCategoryId()) == null) {
            throw new ApiException("Category not found");
        }

        // تنظيف بسيط
        body.setCertificateName(body.getCertificateName().trim());
        body.setProvider(body.getProvider().trim());

        // تثبيت المالك
        body.setContributorId(contributorId);

        // (اختياري) منع تكرار نفس الشهادة لنفس المساهم:
        // لو أضفت في الريبو: existsByContributorIdAndCertificateNameIgnoreCaseAndProviderIgnoreCase(...)
        // if (experienceRepository.existsByContributorIdAndCertificateNameIgnoreCaseAndProviderIgnoreCase(
        //        contributorId, body.getCertificateName(), body.getProvider())) {
        //     throw new ApiException("You already posted an experience for this certificate/provider");
        // }

        experienceRepository.save(body);
    }

    @Transactional
    // UPDATE — فقط CONTRIBUTOR وصاحب التجربة
    public void update(Integer contributorId, Integer experienceId, CertificationExperience updated) {
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");

        CertificationExperience existing = experienceRepository.findCertificationExperienceById(experienceId);
        if (existing == null) throw new ApiException("Experience not found");

        if (!existing.getContributorId().equals(contributorId)) {
            throw new ApiException("You can only update your own experiences");
        }

        // تحديث الحقول المسموح بها (حدّث اللي وصلك فقط)
        if (updated.getCategoryId() != null) {
            if (categoryRepository.findCategoryById(updated.getCategoryId()) == null) {
                throw new ApiException("Category not found");
            }
            existing.setCategoryId(updated.getCategoryId());
        }

        if (updated.getCertificateName() != null) existing.setCertificateName(updated.getCertificateName().trim());
        if (updated.getProvider() != null) existing.setProvider(updated.getProvider().trim());
        if (updated.getPrice() != null) existing.setPrice(updated.getPrice());
        if (updated.getQuestionCount() != null) existing.setQuestionCount(updated.getQuestionCount());
        if (updated.getCertificateDescription() != null) existing.setCertificateDescription(updated.getCertificateDescription());

        if (updated.getScoreAchieved() != null) existing.setScoreAchieved(updated.getScoreAchieved());
        if (updated.getRating() != null) existing.setRating(updated.getRating());
        if (updated.getExperienceDescription() != null) existing.setExperienceDescription(updated.getExperienceDescription());

        experienceRepository.save(existing);
    }

    @Transactional
    // DELETE — فقط CONTRIBUTOR وصاحب التجربة
    public void delete(Integer contributorId, Integer experienceId) {
        Contributor c = contributorRepository.findContributorById(contributorId);
        if (c == null) throw new ApiException("Contributor not found");

        CertificationExperience existing = experienceRepository.findCertificationExperienceById(experienceId);
        if (existing == null) throw new ApiException("Experience not found");

        if (!existing.getContributorId().equals(contributorId)) {
            throw new ApiException("You can only delete your own experiences");
        }

        experienceRepository.delete(existing);
    }


    public List<CertificationExperience> getByCategory(Integer categoryId) {
        if (categoryId == null || categoryRepository.findCategoryById(categoryId) == null) {
            throw new ApiException("Category not found");
        }
        return experienceRepository.findAllByCategoryId(categoryId);
    }

    public List<CertificationExperience> getExperiencesWithScoreAbove(Double score) {
        return experienceRepository.findByScoreAchievedGreaterThan(score);
    }

    // CertificateExperienceService
    public List<CertificationExperience> getByContributor(Integer contributorId) {
        if (contributorRepository.findContributorById(contributorId) == null) {
            throw new ApiException("Contributor not found");
        }
        return experienceRepository.findAllByContributorId(contributorId);
    }


    public List<CertificationExperience> getByProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            throw new ApiException("Provider is required");
        }
        return experienceRepository.findAllByProviderIgnoreCase(provider.trim());
        // أو للبحث الجزئي:
        // return experienceRepository.findAllByProviderContainingIgnoreCase(provider.trim());
    }


    // فلترة بالتكلفة عبر Path Vars
    public List<CertificationExperience> filterByPrice(Double min, Double max) {
        if (min == null || max == null) throw new ApiException("Price range is required");
        if (min < 0) throw new ApiException("Min price cannot be negative");
        if (min > max) throw new ApiException("Min price cannot be greater than max price");
        return experienceRepository.findAllByPriceBetween(min, max);
    }

    // فلترة بعدد الأسئلة عبر Path Vars
    public List<CertificationExperience> filterByQuestionCount(Integer min, Integer max) {
        if (min == null || max == null) throw new ApiException("Question count range is required");
        if (min < 1) throw new ApiException("Min question count must be at least 1");
        if (min > max) throw new ApiException("Min question count cannot be greater than max");
        return experienceRepository.findAllByQuestionCountBetween(min, max);
    }











}