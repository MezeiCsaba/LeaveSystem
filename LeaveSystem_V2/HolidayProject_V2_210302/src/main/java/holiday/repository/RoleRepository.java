package holiday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import holiday.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	
	Role findByRoleName(String role);
	
	Role findByUsersIdAndRoleName(Long userId, String role);
}
