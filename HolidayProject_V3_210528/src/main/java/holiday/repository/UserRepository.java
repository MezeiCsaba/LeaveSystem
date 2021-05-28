package holiday.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import holiday.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	
	List<User> findAllByOrderByName();
	
	User findFirstByNameIgnoreCase(String name);
	
	Page<User> findByNameContainingIgnoreCaseOrderByName(String name, Pageable pageable);
	Page<User> findByNameContainingIgnoreCaseOrderByStatus(String name, Pageable pageable);
	Page<User> findByNameContainingIgnoreCaseOrderByRoles(String name, Pageable pageable);
	Page<User> findByNameContainingIgnoreCaseOrderByEmail(String name, Pageable pageable);
	
	User findByEmail(String Email); 
	
	User findFirstById(Long id);
	
	Page<User> findByOrderByName(Pageable pageable);
	
	
	List<User> findByStatus(Boolean status);
	
	List<User> findAllByApproverId(Long authUserId);
	
	User findFirstByActivationCode(String code);
	
	

	
	
//	@Modifying(clearAutomatically=true)  // userUpdateByAdmin
//	@Transactional
//	@Query("update User u set u.name=:name, u.email=:email, u.password=:password, u.roles=:roles, u.status=:status where id=:id")
//	void updateUserAsAdmin(@Param(value="name") String name,
//			@Param(value="email") String email,
//			@Param(value="password") String password,
//			@Param(value="roles") Set<Role> roles,
//			@Param(value="status") Boolean status,
//			@Param(value="id") Long id);
//	
	
//	@Modifying(clearAutomatically=true)   // userUpdateByHR
//	@Transactional
//	@Query("update User u set u.userLeaves=:userLeaves, u.status=:status where id=:id")
//	void updateUserAsHR(@Param(value="userLeaves") Set<UserLeaves> userLeaves,
//			@Param(value="status") Boolean status,
//			@Param(value="id") Long id);

	
	
}
