package vn.compedia.api.controller.cms;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.compedia.api.ApiResponseDto;
import vn.compedia.api.dto.VietTienPageDto;
import vn.compedia.api.dto.VietTienResponseDto;
import vn.compedia.api.exception.GlobalExceptionHandler;
import vn.compedia.api.exception.authentication.PasswordsDontMatchException;
import vn.compedia.api.exception.changepassword.*;
import vn.compedia.api.exception.user.EmailNotFoundException;
import vn.compedia.api.request.AccountUpdateRequest;
import vn.compedia.api.request.AdminCreateRequest;
import vn.compedia.api.request.ChangePassRequest;
import vn.compedia.api.response.admin.AccountNeResponse;
import vn.compedia.api.response.admin.AdminResponse;
import vn.compedia.api.service.AccountService;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Account")
@RestController
@RequestMapping("/api/v1/account")
@Validated
@Log4j2
public class AccountController extends GlobalExceptionHandler {

    @Autowired
    private AccountService accountService;

    @GetMapping(value = "account")
    public ResponseEntity<?> getAll() {
        List<AccountNeResponse> list = accountService.getAll();
        return VietTienResponseDto.ok(list, "Get list account success");
    }

    @GetMapping(value = "get-one")
    public ResponseEntity<?> getOne() {
        try {
            AccountNeResponse admin = accountService.getOne();
            return VietTienResponseDto.ok(admin, "Get list account success");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "search")
    public ResponseEntity<?> search(@RequestParam(name = "username", required = false) String username,
                                    @RequestParam(name = "email", required = false) String email,
                                    @RequestParam(name = "roleId", required = false) Integer roleId,
                                    @RequestParam(name = "page") Integer page,
                                    @RequestParam(name = "size") Integer size,
                                    @RequestParam(name = "codeRole", required = false) String codeRole,
                                    @RequestParam(name = "fullName", required = false) String fullName,
                                    @RequestParam(name = "sortField", required = false) String sortField,
                                    @RequestParam(name = "sortOrder", required = false) String sortOrder) {
        Page<AdminResponse> list = accountService.search(username, email, roleId, codeRole, fullName, sortField, sortOrder, page, size);
        return VietTienResponseDto.ok(VietTienPageDto.build(list), "Search list book success");

    }


    @PostMapping()
    public ResponseEntity<?> create(@RequestBody AdminCreateRequest request) {
        try {
            accountService.create(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return VietTienResponseDto.ok("", "Save success");
    }

    @PostMapping(value = "register")
    public ResponseEntity<?> register (@RequestBody AdminCreateRequest request) {
        try {
            accountService.register(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return VietTienResponseDto.ok("", "????ng k?? th??nh c??ng ");
    }

    @PutMapping()
    public ResponseEntity<?> update(@RequestBody AdminCreateRequest request) {
        try {
            accountService.update(request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return VietTienResponseDto.ok("", "Save success");
    }

    @PutMapping(value = "updatePass")
    public ResponseEntity<?> updatePass(@RequestBody AccountUpdateRequest request) {
        accountService.updatePass(request);
        return VietTienResponseDto.ok("", "Save success");
    }

    @DeleteMapping()
    public ResponseEntity<?> delete(@RequestParam Long id) {
        accountService.delete(id);
        return VietTienResponseDto.ok("", "Save success");
    }

    @PutMapping("change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassRequest request) {
        try {
            accountService.changePassword(request);
            return ApiResponseDto.createdWithMessage("?????i m???t kh???u th??nh c??ng!", HttpStatus.OK);
        } catch (PasswordOldNotValid e) {
            return ApiResponseDto.createdWithMessage("M???t kh???u c?? kh??ng ????ng", HttpStatus.BAD_REQUEST);
        } catch (PasswordsDontMatchException e) {
            return ApiResponseDto.createdWithMessage("M???t kh???u kh??ng kh???p", HttpStatus.BAD_REQUEST);
        } catch (PasswordNewNotMathRegex passwordNewNotMathRegex) {
            return ApiResponseDto.createdWithMessage("M???t kh???u m???i ph???i c?? ??t nh???t 6 k?? t???, bao g???m k?? t??? s???, ch??? in hoa, ch??? in th?????ng", HttpStatus.BAD_REQUEST);
        } catch (PasswordOldNotFoundException e) {
            return ApiResponseDto.createdWithMessage("M???t kh???u c?? kh??ng ???????c ????? tr???ng", HttpStatus.BAD_REQUEST);
        } catch (PasswordNewNotFoundException e) {
            return ApiResponseDto.createdWithMessage("M???t kh???u m???i kh??ng ???????c ????? tr???ng", HttpStatus.BAD_REQUEST);
        } catch (NewPasswordMatchOldPassword e) {
            return ApiResponseDto.createdWithMessage("M???t kh???u m???i kh??ng ???????c tr??ng v???i m???t kh???u c??", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam String email) {
        try {
            accountService.forgetPassword(email);
            return ApiResponseDto.createdWithMessage("Th??nh c??ng", HttpStatus.OK);
        } catch (EmailNotFoundException e) {
            log.error(e.getMessage());
            return ApiResponseDto.createdWithMessage("Kh??ng t??m th???y email!", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ApiResponseDto.createdWithMessage("Email kh??ng t???n t???i!", HttpStatus.NOT_FOUND);
        }
    }
}
