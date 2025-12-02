package com.example.demo.service;

import com.example.demo.dto.SprintDTO;
import com.example.demo.dto.SprintReport;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Project;
import com.example.demo.model.Sprint;
import com.example.demo.model.SprintStatus;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service class for managing Sprint operations.
 * Handles sprint creation, updates, retrieval, and validation.
 */
@Service
@Transactional
public class SprintService {
    
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final ReportService reportService;
    
    @Autowired
    public SprintService(SprintRepository sprintRepository, ProjectRepository projectRepository, ReportService reportService) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.reportService = reportService;
    }
    
    /**
     * Create a new sprint with date validation.
     * Validates that dates are valid and do not overlap with existing sprints.
     * 
     * @param sprintDTO the sprint data transfer object
     * @return the created Sprint entity
     * @throws ValidationException if sprint data is invalid or dates overlap
     * @throws ResourceNotFoundException if project is not found
     */
    public Sprint createSprint(SprintDTO sprintDTO) {
        // Validate input
        if (sprintDTO == null) {
            throw new ValidationException("Sprint data cannot be null");
        }
        
        if (sprintDTO.getName() == null || sprintDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Sprint name is required");
        }
        
        if (sprintDTO.getProjectId() == null) {
            throw new ValidationException("Project ID is required");
        }
        
        if (sprintDTO.getStartDate() == null) {
            throw new ValidationException("Start date is required");
        }
        
        if (sprintDTO.getEndDate() == null) {
            throw new ValidationException("End date is required");
        }
        
        // Validate end date is after start date
        if (!sprintDTO.getEndDate().after(sprintDTO.getStartDate())) {
            throw new ValidationException("End date must be after start date");
        }
        
        // Get project
        Project project = projectRepository.findById(sprintDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", sprintDTO.getProjectId()));
        
        // Validate no overlapping sprint dates
        validateNoOverlappingSprints(project.getId(), sprintDTO.getStartDate(), sprintDTO.getEndDate(), null);
        
        // Create new sprint
        Sprint sprint = new Sprint();
        sprint.setName(sprintDTO.getName());
        sprint.setProject(project);
        sprint.setStartDate(sprintDTO.getStartDate());
        sprint.setEndDate(sprintDTO.getEndDate());
        sprint.setStatus(SprintStatus.PLANNED);
        
        return sprintRepository.save(sprint);
    }
    
    /**
     * Update an existing sprint's information.
     * Validates date changes to prevent overlaps.
     * 
     * @param id the sprint ID
     * @param sprintDTO the updated sprint data
     * @return the updated Sprint entity
     * @throws ResourceNotFoundException if sprint is not found
     * @throws ValidationException if sprint data is invalid
     */
    public Sprint updateSprint(Long id, SprintDTO sprintDTO) {
        if (id == null) {
            throw new ValidationException("Sprint ID cannot be null");
        }
        
        if (sprintDTO == null) {
            throw new ValidationException("Sprint data cannot be null");
        }
        
        Sprint existingSprint = getSprintById(id);
        
        // Update name if provided
        if (sprintDTO.getName() != null && !sprintDTO.getName().trim().isEmpty()) {
            existingSprint.setName(sprintDTO.getName());
        }
        
        // Update dates if provided
        Date newStartDate = sprintDTO.getStartDate() != null ? sprintDTO.getStartDate() : existingSprint.getStartDate();
        Date newEndDate = sprintDTO.getEndDate() != null ? sprintDTO.getEndDate() : existingSprint.getEndDate();
        
        // Validate end date is after start date
        if (!newEndDate.after(newStartDate)) {
            throw new ValidationException("End date must be after start date");
        }
        
        // Validate no overlapping sprint dates (excluding current sprint)
        validateNoOverlappingSprints(existingSprint.getProject().getId(), newStartDate, newEndDate, id);
        
        existingSprint.setStartDate(newStartDate);
        existingSprint.setEndDate(newEndDate);
        
        return sprintRepository.save(existingSprint);
    }
    
    /**
     * Get a sprint by its ID.
     * 
     * @param id the sprint ID
     * @return the Sprint entity
     * @throws ResourceNotFoundException if sprint is not found
     */
    @Transactional(readOnly = true)
    public Sprint getSprintById(Long id) {
        if (id == null) {
            throw new ValidationException("Sprint ID cannot be null");
        }
        
        return sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", id));
    }
    
    /**
     * Get all sprints for a specific project.
     * 
     * @param projectId the project ID
     * @return list of sprints in the project
     * @throws ResourceNotFoundException if project is not found
     */
    @Transactional(readOnly = true)
    public List<Sprint> getSprintsByProject(Long projectId) {
        if (projectId == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        // Verify project exists
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        
        return sprintRepository.findByProjectId(projectId);
    }
    
    /**
     * Validate that a sprint's date range does not overlap with existing sprints in the same project.
     * 
     * @param projectId the project ID
     * @param startDate the sprint start date
     * @param endDate the sprint end date
     * @param excludeSprintId the sprint ID to exclude from validation (for updates), null for new sprints
     * @throws ValidationException if dates overlap with an existing sprint
     */
    private void validateNoOverlappingSprints(Long projectId, Date startDate, Date endDate, Long excludeSprintId) {
        List<Sprint> existingSprints = sprintRepository.findByProjectId(projectId);
        
        for (Sprint existingSprint : existingSprints) {
            // Skip the sprint being updated
            if (excludeSprintId != null && existingSprint.getId().equals(excludeSprintId)) {
                continue;
            }
            
            Date existingStart = existingSprint.getStartDate();
            Date existingEnd = existingSprint.getEndDate();
            
            // Check for overlap
            // Overlap occurs if:
            // 1. New sprint starts during existing sprint: startDate >= existingStart && startDate < existingEnd
            // 2. New sprint ends during existing sprint: endDate > existingStart && endDate <= existingEnd
            // 3. New sprint completely contains existing sprint: startDate <= existingStart && endDate >= existingEnd
            // 4. Existing sprint completely contains new sprint: startDate >= existingStart && endDate <= existingEnd
            
            boolean overlaps = (startDate.compareTo(existingStart) >= 0 && startDate.before(existingEnd)) ||
                              (endDate.after(existingStart) && endDate.compareTo(existingEnd) <= 0) ||
                              (startDate.compareTo(existingStart) <= 0 && endDate.compareTo(existingEnd) >= 0);
            
            if (overlaps) {
                throw new ValidationException(
                    String.format("Sprint dates overlap with existing sprint '%s' (%s to %s)",
                        existingSprint.getName(),
                        existingSprint.getStartDate(),
                        existingSprint.getEndDate())
                );
            }
        }
    }
    
    /**
     * Complete a sprint and automatically generate a final sprint report.
     * Changes the sprint status to COMPLETED and triggers report generation.
     * 
     * @param id the sprint ID
     * @return the generated SprintReport
     * @throws ResourceNotFoundException if sprint is not found
     * @throws ValidationException if sprint is already completed
     */
    public SprintReport completeSprint(Long id) {
        if (id == null) {
            throw new ValidationException("Sprint ID cannot be null");
        }
        
        Sprint sprint = getSprintById(id);
        
        // Check if sprint is already completed
        if (sprint.getStatus() == SprintStatus.COMPLETED) {
            throw new ValidationException("Sprint is already completed");
        }
        
        // Update sprint status to COMPLETED
        sprint.setStatus(SprintStatus.COMPLETED);
        sprintRepository.save(sprint);
        
        // Generate and return sprint report
        return reportService.generateSprintReport(id);
    }
}
