package sideeffect.project.dto.user;

import lombok.*;
import sideeffect.project.domain.applicant.Applicant;
import sideeffect.project.domain.applicant.ApplicantStatus;
import sideeffect.project.domain.position.PositionType;
import sideeffect.project.domain.user.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class ApplyBoardResponse {

    private Long applicationId;
    private String title;
    private PositionType position;
    private ApplicantStatus status;
    private Boolean isRecruiting;

    public static List<ApplyBoardResponse> listOf(User user){

        List<ApplyBoardResponse> applyBoardResponseList = Collections.emptyList();
        List<Applicant> applicants = user.getApplicants();
        if(applicants!=null && !applicants.isEmpty()) {
            applyBoardResponseList = applicants.stream()
                    .map(applicant -> getApplyBoardResponse(user, applicant))
                    .collect(Collectors.toList());
        }

        return applyBoardResponseList;
    }

    private static ApplyBoardResponse getApplyBoardResponse(User user, Applicant applicant) {
        return ApplyBoardResponse.builder()
                .applicationId(applicant.getBoardPosition().getRecruitBoard().getId())
                .title(applicant.getBoardPosition().getRecruitBoard().getTitle())
                .position(user.getPosition())
                .status(applicant.getStatus())
                .isRecruiting(true)
                .build();
    }

}
