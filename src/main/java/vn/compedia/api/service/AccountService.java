package vn.compedia.api.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.validator.ValidatorException;
import vn.compedia.api.entity.Account;
import vn.compedia.api.entity.User;
import vn.compedia.api.exception.authentication.PasswordsDontMatchException;
import vn.compedia.api.exception.changepassword.*;
import vn.compedia.api.exception.user.EmailNotFoundException;
import vn.compedia.api.repository.AccountRepository;
import vn.compedia.api.repository.AdminRepository;
import vn.compedia.api.request.AccountUpdateRequest;
import vn.compedia.api.request.AdminCreateRequest;
import vn.compedia.api.request.ChangePassRequest;
import vn.compedia.api.response.admin.AccountNeResponse;
import vn.compedia.api.response.admin.AdminResponse;
import vn.compedia.api.util.DateUtil;
import vn.compedia.api.util.DbConstant;
import vn.compedia.api.util.EmailUtil;
import vn.compedia.api.util.StringUtil;
import vn.compedia.api.util.user.UserContextHolder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Log4j2
@Service
public class AccountService {


    @Value("${validate.phone_number.regex}")
    private String phoneNumberRegex;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AdminRepository adminRepository;

    public List<AccountNeResponse> getAll() {
        return accountRepository.getAllAccount();
    }

    public AccountNeResponse getOne() throws Exception {
        Optional<AccountNeResponse> admin = accountRepository.findByAccountId();
        if (!admin.isPresent()) {
            throw new Exception(" EMPTY");
        }
        return admin.get();
    }

    public void validateData(AdminCreateRequest request) throws Exception {
        if (!StringUtil.validateEmail(request.getEmail())) {
            throw new Exception("?????a ch??? email kh??ng ????ng ?????nh d???ng");
        }
        Optional<Account> optionalAccount = findAccountByEmailAndUserName(request.getEmail());
        if (optionalAccount.isPresent()) {
            throw new Exception("?????a ch??? email n??y ???? t???n t???i");
        }
        if (!StringUtil.validatePassword(request.getPassword())) {
            throw new Exception("Password kh??ng ????ng ?????nh d???ng, m???t kh???u ph???i c?? t??m k?? t??? bao g???m m???t k?? t??? hoa, m???t k?? t??? ?????c bi???t v?? c??c k?? t??? ch??? v?? s???");
        }
        if (!StringUtil.validateUser(request.getUsername())) {
            throw new Exception("User kh??ng ????ng ?????nh d???ng");
        }
        if (!StringUtil.isValid(request.getUsername())) {
            throw new Exception("Nh???p l???i User c?? ????? d??i t??? 5-30 k?? t???");
        }
        if (StringUtils.isBlank(request.getFullName().trim())) {
            throw new Exception("Kh??ng ???????c ????? tr???ng FullName!");
        }
        if (request.getFullName().trim().length() > 50) {
            throw new Exception("V?????t qu?? 50 k?? t???, y??u c???u nh???p l???i fullName");
        }
        if (StringUtils.isBlank(request.getPhone().trim())) {
            throw new Exception("Kh??ng ???????c ????? tr???ng PhoneNumber");
        }
        if (request.getPhone().trim().length() > 11) {
            throw new Exception("Y??u c???u nh???p l???i phoneNumber kh??ng qu?? 11 k?? t???");
        }
        if (!request.getPhone().trim().matches(phoneNumberRegex)) {
            throw new Exception("Kh??ng ????ng ?????nh d???ng phoneNumber");
        }
        if (StringUtils.isBlank(request.getEmail().trim())) {
            throw new Exception("Kh??ng ???????c ????? tr???ng Email");
        }
        if (request.getEmail().trim().length() > 100) {
            throw new Exception("Y??u c???u nh???p l???i Email c?? ????? d??i kh??ng qu?? 100 k?? t???");
        }
        if (StringUtils.isNotBlank(request.getDOB().trim())) {
            if (!DateUtil.isValid(request.getDOB())) {
                throw new ValidatorException("birthday is not in the correct format (yyyy-MM-dd)");
            }
        }
    }


    public void create(AdminCreateRequest request) throws Exception {
        validateData(request);
        Account account = new Account();
        account.setPassword(StringUtil.encryptPassword(request.getPassword(), account.getSalt()));
        account.setUsername(request.getUsername());
        account.setRoleId(request.getRoleId());
        account.setCreateDate(new Date());
        account.setCreateBy(UserContextHolder.getUser().getAccountId());
        account.setEmail(request.getEmail());
        account.setFullName(request.getFullName());
        account.setDOB(request.getDOB());
        account.setPhone(request.getPhone());
        account.setStatus(1);
        accountRepository.save(account);
    }

    public void update(AdminCreateRequest request) throws Exception {
        validateData2(request);
        Account account = accountRepository.findById(request.getId()).get();

//        if (StringUtils.isNotBlank(request.getPassword())) {
//            account.setPassword(StringUtil.encryptPassword(request.getPassword(), account.getSalt()));
//        }
        account.setUsername(request.getUsername());
        account.setRoleId(request.getRoleId());
        account.setUpdateDate(new Date());
        account.setCreateBy(UserContextHolder.getUser().getAccountId());
        account.setFullName(request.getFullName());
        account.setDOB(request.getDOB());
        account.setEmail(request.getEmail());
        account.setStatus(1);
        account.setPhone(request.getPhone());
        accountRepository.save(account);
    }

    public void updatePass(AccountUpdateRequest request)  {
//        Random r = new Random();
        Account account = accountRepository.findById(request.getId()).get();
        account.setPassword(StringUtil.encryptPassword(DbConstant.PASSWORD, account.getSalt()));
        accountRepository.save(account);
    }

    public void validateData2(AdminCreateRequest request) throws Exception {
        if (!StringUtil.validateEmail(request.getEmail())) {
            throw new Exception("?????a ch??? email kh??ng ????ng ?????nh d???ng");
        }
//
//        if (!StringUtil.validatePassword(request.getPassword())) {
//            throw new Exception("Password kh??ng ????ng ?????nh d???ng, m???t kh???u ph???i c?? t??m k?? t??? bao g???m m???t k?? t??? hoa, m???t k?? t??? ?????c bi???t v?? c??c k?? t??? ch??? v?? s???");
//        }
        if (!StringUtil.validateUser(request.getUsername())) {
            throw new Exception("User kh??ng ????ng ?????nh d???ng");
        }
        if (!StringUtil.isValid(request.getUsername())) {
            throw new Exception("Nh???p l???i User c?? ????? d??i t??? 5-30 k?? t???");
        }
        if (StringUtils.isBlank(request.getFullName().trim())) {
            throw new Exception("Kh??ng ???????c ????? tr???ng!");
        }
        if (request.getFullName().trim().length() > 50) {
            throw new Exception("V?????t qu?? 50 k?? t???, y??u c???u nh???p l???i fullName");
        }
        if (StringUtils.isBlank(request.getPhone().trim())) {
            throw new Exception("Kh??ng ???????c ????? tr???ng");
        }
        if (request.getPhone().trim().length() > 20) {
            throw new Exception("Y??u c???u nh???p l???i phoneNumber kh??ng qu?? 20 k?? t???");
        }
        if (StringUtils.isBlank(request.getEmail().trim())) {
            throw new Exception("Kh??ng ???????c ????? tr???ng");
        }
        if (request.getEmail().trim().length() > 100) {
            throw new Exception("Y??u c???u nh???p l???i Email c?? ????? d??i kh??ng qu?? 100 k?? t???");
        }
    }

    public void delete(Long id) {

        accountRepository.deleteById(id);
    }

    public Page<AdminResponse> search(String username, String email, Integer roleId, String codeRole, String fullName, String sortField, String sortOrder, Integer page, Integer size) {
        return adminRepository.search(username, email, roleId, codeRole, fullName, sortField, sortOrder, page, size, PageRequest.of(page, size));
    }

    private Optional<Account> findAccountByEmailAndUserName(String email) {
        return accountRepository.findAccountByEmailAndUserName(email);
    }

    @Transactional
    public void changePassword(ChangePassRequest request) throws PasswordOldNotValid, PasswordNewNotMathRegex, PasswordOldNotFoundException, PasswordNewNotFoundException, NewPasswordMatchOldPassword {
        Optional<Account> account = accountRepository.findByUsername(UserContextHolder.getUser().getAccountId());
        if (!account.get().getPassword().equals(StringUtil.encryptPassword(request.getOldPassword(), account.get().getSalt()))) {
            throw new PasswordOldNotValid();
        }
        if (!request.getNewPassword().matches(StringUtil.PATTERN_PASSWORD)) {
            throw new PasswordNewNotMathRegex();
        }
        if (!request.getNewPassword().equals(request.getRePassword())) {
            throw new PasswordsDontMatchException();
        }
        if (StringUtils.isBlank(request.getOldPassword())) {
            throw new PasswordOldNotFoundException();
        }
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new PasswordNewNotFoundException();
        }
        if (request.getNewPassword().equals(request.getOldPassword())) {
            throw new NewPasswordMatchOldPassword();
        }
        account.get().setSalt(StringUtil.generateSalt());
        account.get().setPassword(StringUtil.encryptPassword(request.getNewPassword(), account.get().getSalt()));
        accountRepository.save(account.get());
    }
    public void forgetPassword(String email) throws EmailNotFoundException {
        Account account = adminRepository.findByEmail(email).orElseThrow(EmailNotFoundException::new);
        account.setSalt(StringUtil.generateSalt());
        account.setPassword(StringUtil.encryptPassword(account.getSalt(), account.getSalt()));
        adminRepository.save(account);
        EmailUtil.getInstance().sendForgetPassword(email, account.getSalt());
    }
    public void register (AdminCreateRequest request) throws Exception {
        validateData(request);
        Account account = new Account();
        account.setPassword(StringUtil.encryptPassword(request.getPassword(), account.getSalt()));
        account.setUsername(request.getUsername());
        account.setRoleId(DbConstant.ROLE_ID_USER);
        account.setCreateDate(new Date());
        account.setEmail(request.getEmail());
        account.setFullName(request.getFullName());
        account.setDOB(request.getDOB());
        account.setPhone(request.getPhone());
        account.setStatus(DbConstant.STATUS_ACTIVE);
        accountRepository.save(account);
    }
}


