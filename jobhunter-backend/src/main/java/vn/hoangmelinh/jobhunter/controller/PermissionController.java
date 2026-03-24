package vn.hoangmelinh.jobhunter.controller;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoangmelinh.jobhunter.domain.Permission;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.service.PermissionService;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;
import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {

        // check exist permission
        if (permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission has already");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {

        // check exist permission
        if (this.permissionService.fetchById(permission.getId()) == null) {
            throw new IdInvalidException("Permission not found");
        }

        // check exist by module, apiPath, method
        if (this.permissionService.isPermissionExist(permission)) {
            // check name
            if (this.permissionService.isSameName(permission)) {
                throw new IdInvalidException("Permission has already");
            }
        }

        // update permission
        return ResponseEntity.ok(permissionService.updatePermission(permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) throws IdInvalidException {
        // check exist permission
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission not found");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.fetchAllPermissions(spec, pageable));
    }
}
