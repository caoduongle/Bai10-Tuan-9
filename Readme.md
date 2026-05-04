# BÁO CÁO BÀI 10 - THE BROKEN PIPELINE

**Sinh viên:** Cao Dương Lễ
**Link GitHub Repository:** [https://github.com/caoduongle/Bai10-Tuan-9](https://github.com/caoduongle/Bai10-Tuan-9)

---

## 🚨 Lỗi 1: Máy chủ CI không có mã nguồn (Missing Checkout)

*   **Vị trí lỗi:** File `.github/workflows/ci.yml` (Thiếu step checkout ở dòng 10).
*   **Log minh chứng (Trích xuất từ GitHub Actions):**
    ```text
    Error:  The goal you specified requires a project to execute but there is no POM in this directory (/home/runner/work/Bai10-Tuan-9/Bai10-Tuan-9). Please verify you invoked Maven from the correct directory. -> [Help 1]
    ```
*   **Nguyên nhân kỹ thuật:** Khi GitHub Actions khởi tạo máy ảo `ubuntu-latest` để chạy CI, máy ảo này hoàn toàn trống rỗng. Lệnh `mvn package` chạy ngay lập tức mà không tìm thấy file `pom.xml` nào để thực thi vì mã nguồn chưa được kéo (clone) từ GitHub về máy ảo.
*   **Cách khắc phục:** Thêm action `actions/checkout@v3` vào đầu danh sách steps để máy ảo tải code về trước khi cài đặt JDK và chạy Maven.

---

## 🚨 Lỗi 2 & 3: Sai phiên bản thư viện và Lỗi tương thích Java (Dependency & Surefire Version)

*   **Vị trí lỗi:** File `pom.xml`.
    *   Thư viện `logback-classic` khai báo phiên bản ảo `9.9.9`.
    *   Plugin `maven-surefire-plugin` dùng phiên bản cũ `2.12.4` không hỗ trợ Java 17.
*   **Nguyên nhân kỹ thuật:** Thư viện không tồn tại trên Maven Central sẽ khiến quá trình tải dependency thất bại. Ngoài ra, Surefire bản cũ không thể đọc được bytecode major version 61 của Java 17.
*   **Cách khắc phục:** Cập nhật phiên bản `logback-classic` lên `1.4.11` và `maven-surefire-plugin` lên `3.2.5`. Đồng thời chỉnh Java compiler về target 17 để đồng bộ với CI. *(Ghi chú: Lỗi này được phát hiện và sửa đồng thời trong quá trình cấu trúc lại file POM).*

---

## 🚨 Lỗi Phát sinh thực tế (Unrecognised Tag XML)

*   **Vị trí lỗi:** File `pom.xml` (Dòng 29, cấu trúc XML bị lồng sai).
*   **Log minh chứng (Trích xuất từ GitHub Actions):**
    ```text
    Error: Some problems were encountered while processing the POMs:
    Error: Malformed POM /home/runner/work/Bai10-Tuan-9/Bai10-Tuan-9/pom.xml: Unrecognised tag: 'build' (position: START_TAG seen ...</dependency>\n<build>... @29:16) @ /home/runner/work/Bai10-Tuan-9/Bai10-Tuan-9/pom.xml, line 29, column 16
    ```
*   **Nguyên nhân kỹ thuật:** Trong quá trình thiết lập file `pom.xml`, thẻ `<build>` đã bị đặt nhầm vào bên trong thẻ `<dependencies>`. Maven yêu cầu tuân thủ cấu trúc XML nghiêm ngặt, việc lồng sai thẻ khiến Maven không thể phân tích (parse) tệp cấu hình và lập tức dừng tiến trình build.
*   **Cách khắc phục:** Di chuyển khối `<build>` ra ngoài, đứng ngang hàng và độc lập với khối `<dependencies>`.

---

## 🚨 Lỗi 4 (Lỗi tự tạo): Sai logic nghiệp vụ làm hỏng Unit Test

*   **Vị trí tạo lỗi:** File `src/main/java/com/lab/ShippingCalculator.java` (Cố tình sửa công thức tính cước gói STANDARD làm sai lệch kết quả).
*   **Log minh chứng (Trích xuất từ GitHub Actions):**
    ```text
    Error:  Tests run: 3, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.054 s <<< FAILURE! -- in com.lab.ShippingCalculatorTest
    Error:  com.lab.ShippingCalculatorTest.testStandard -- Time elapsed: 0.003 s <<< FAILURE!
    org.opentest4j.AssertionFailedError: expected: <15000.0> but was: <20000.0>
    ...
    Error:  Failures: 
    Error:    ShippingCalculatorTest.testStandard:12 expected: <15000.0> but was: <20000.0>
    ```
*   **Nguyên nhân kỹ thuật:** Mã nguồn xử lý nghiệp vụ đã bị thay đổi làm kết quả đầu ra trả về `20000.0`, sai lệch so với kỳ vọng `15000.0` (Assertion) được viết chặt chẽ trong file Unit Test `ShippingCalculatorTest.java`. Hệ thống CI đóng vai trò chốt chặn, lập tức đánh dấu quy trình Build thất bại để ngăn chặn việc đưa code sai logic này lên môi trường thực tế.
*   **Cách khắc phục:** Trả lại đúng công thức toán học ban đầu `return weight * 3000;` trong file `ShippingCalculator.java` để đảm bảo logic hoạt động chuẩn xác và vượt qua bài Unit Test.