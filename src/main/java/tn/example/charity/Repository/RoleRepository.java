package tn.example.charity.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.example.charity.Entity.Role;
import tn.example.charity.Entity.URole;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<URole, Integer> {

    Optional<URole> findByRole(Role role);

    URole getByRole(Role role);


}
