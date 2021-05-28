package holiday.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import holiday.entity.Role;
import holiday.entity.User;
import holiday.repository.RoleRepository;

@Service

public class RoleService {
	
	private RoleRepository roleRepo;
	
	@Autowired
	public void setRoleRepo(RoleRepository roleRepo) {
		this.roleRepo = roleRepo;
	}
	
	public Boolean userRoleisExist(Long userId,String roleName) {
		Boolean isEx = false;
		if (roleRepo.findByUsersIdAndRoleName(userId, roleName)!=null) isEx=true;
		return isEx;
	}
	
	
	public List<User> findUsersByRoleName(String roleName){
		
		List<User> userList=new ArrayList<>();
		
		Role role=roleRepo.findByRoleName(roleName);
		
		for (User user:role.getUsers()) {
			if (user.getStatus()) userList.add(user);  // 'aktív' státuszú jóváhagyók összegyűjtése
		}
		
		return userList;
	}
	
	
	public List<Role> getAllRoles(){
		return roleRepo.findAll();
	}
	
	
	
	
	

}
