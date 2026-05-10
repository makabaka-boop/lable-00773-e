package com.finance.controller;

import com.finance.common.Result;
import com.finance.entity.Voucher;
import com.finance.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    
    private final VoucherService voucherService;

    @GetMapping
    public Result<List<Voucher>> list(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String voucherNo) {
        return Result.success(voucherService.findByCondition(period, status, voucherNo));
    }
    
    @GetMapping("/page")
    public Result<java.util.Map<String, Object>> page(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String voucherNo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(voucherService.findByConditionPage(period, status, voucherNo, page, size));
    }

    @GetMapping("/{id}")
    public Result<Voucher> getById(@PathVariable Long id) {
        return Result.success(voucherService.findById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Voucher voucher) {
        try {
            voucherService.save(voucher);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Voucher voucher) {
        try {
            voucher.setId(id);
            voucherService.save(voucher);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/post")
    public Result<Void> post(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            voucherService.post(id, body.get("reviewer"));
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/void")
    public Result<Void> voidVoucher(@PathVariable Long id) {
        try {
            voucherService.voidVoucher(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            voucherService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
