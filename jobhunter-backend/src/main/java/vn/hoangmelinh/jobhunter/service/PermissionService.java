package vn.hoangmelinh.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoangmelinh.jobhunter.domain.Permission;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(),
                permission.getApiPath(),
                permission.getMethod());
    }

    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission fetchById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public boolean isSameName(Permission permission) {
        Permission permissionDB = this.permissionRepository.findById(permission.getId()).get();
        return permissionDB.getName().equals(permission.getName());
    }

    public Permission updatePermission(Permission permission) {
        Permission permissionDB = this.fetchById(permission.getId());

        if (permissionDB != null) {
            permissionDB.setName(permission.getName());
            permissionDB.setModule(permission.getModule());
            permissionDB.setApiPath(permission.getApiPath());
            permissionDB.setMethod(permission.getMethod());

            // update
            permissionDB = this.permissionRepository.save(permissionDB);
            return permissionDB;
        }
        return null;
    }

    public void deletePermission(Long id) {
        // delete permission_role
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));

        // delete permission
        this.permissionRepository.delete(currentPermission);
    }

    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable page) {

        Page<Permission> permissionPage = this.permissionRepository.findAll(spec, page);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(permissionPage.getNumber() + 1);
        mt.setPageSize(permissionPage.getSize());
        mt.setPages(permissionPage.getTotalPages());
        mt.setTotal(permissionPage.getTotalElements());

        resultPaginationDTO.setMeta(mt);
        resultPaginationDTO.setResult(permissionPage.getContent());
        return resultPaginationDTO;
    }
}
