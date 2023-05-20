package sideeffect.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sideeffect.project.domain.freeboard.FreeBoard;
import sideeffect.project.repository.freeboard.FreeBoardRepositoryCustom;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardRepositoryCustom {

    boolean existsByProjectUrl(String projectUrl);

    @Query("SELECT b from FreeBoard b join fetch b.likes where b.id = :boardId")
    Optional<FreeBoard> searchBoardFetchJoinLike(@Param("boardId") Long boardId);
}
