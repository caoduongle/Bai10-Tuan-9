# BÁO CÁO BÀI 10 - THE BROKEN PIPELINE

**Link GitHub Repository:** [https://github.com/caoduongle/Bai10-Tuan-9](https://github.com/caoduongle/Bai10-Tuan-9)

---

## 🚨 Lỗi 1: Máy chủ CI không có mã nguồn (Missing Checkout)

*   **Vị trí lỗi:** File `.github/workflows/ci.yml` (Thiếu step checkout ở dòng 10).
*   **Log minh chứng:**
    ```text
    [ERROR] The goal you specified requires a project to execute but there is no POM in this directory (/). Please verify you invoked Maven from the correct directory.
    Error: Process completed with exit code 1.
    ```
*   **Nguyên nhân kỹ thuật:** Khi GitHub Actions khởi tạo máy ảo `ubuntu-latest` để chạy CI, máy ảo này hoàn toàn trống rỗng. Lệnh `mvn package` chạy ngay lập tức mà không tìm thấy file `pom.xml` nào để thực thi vì mã nguồn chưa được kéo (clone) từ GitHub về máy ảo.
*   **Cách khắc phục:** Thêm action `actions/checkout@v3` vào đầu danh sách steps để máy ảo tải code về trước khi cài đặt JDK và chạy Maven.

---

## 🚨 Lỗi 2: Sai phiên bản thư viện (Dependency Resolution Error)

*   **Vị trí lỗi:** File `pom.xml` (Dòng 18-22, thẻ `<version>9.9.9</version>` của thư viện `logback-classic`).
*   **Log minh chứng:**
    ```text
    [ERROR] Failed to execute goal on project shipping-app: Could not resolve dependencies for project com.lab:shipping-app:jar:1.0-SNAPSHOT: Could not find artifact ch.qos.logback:logback-classic:jar:9.9.9 in central ([https://repo.maven.apache.org/maven2](https://repo.maven.apache.org/maven2)) -> [Help 1]
    ```
*   **Nguyên nhân kỹ thuật:** File POM đang yêu cầu Maven tải thư viện `logback-classic` phiên bản `9.9.9`. Tuy nhiên, phiên bản ảo này không hề tồn tại trên kho lưu trữ trung tâm Maven Central. Quá trình tải dependency thất bại khiến tiến trình build bị hủy bỏ.
*   **Cách khắc phục:** Sửa phiên bản `9.9.9` thành một phiên bản hợp lệ và có thực, ví dụ như `1.4.11`.

---

## 🚨 Lỗi 3: Lỗi tương thích Java (Unsupported Class Version)

*   **Vị trí lỗi:** File `pom.xml` (Dòng 34, thẻ `<version>2.12.4</version>` của `maven-surefire-plugin`).
*   **Log minh chứng:**
    ```text
    [ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.12.4:test (default-test) on project shipping-app: Execution default-test of goal org.apache.maven.plugins:maven-surefire-plugin:2.12.4:test failed: java.lang.IllegalArgumentException: Unsupported class file major version 61 -> [Help 1]
    ```
*   **Nguyên nhân kỹ thuật:** Dự án được biên dịch bằng Java 17 (major version 61). Tuy nhiên, `maven-surefire-plugin` (plugin dùng để chạy Unit Test) đang ở phiên bản quá cũ (`2.12.4`). Phiên bản này không có khả năng đọc và hiểu được cấu trúc bytecode của Java 17, dẫn đến crash khi cố gắng chạy test.
*   **Cách khắc phục:** Nâng cấp `maven-surefire-plugin` lên phiên bản mới hơn, ví dụ `<version>3.2.5</version>`, để hỗ trợ chuẩn Java 17 và kiến trúc của JUnit 5.

---

## 🚨 Lỗi 4 (Lỗi tự tạo): Sai logic nghiệp vụ làm hỏng Unit Test

*   **Vị trí tạo lỗi:** File `src/main/java/com/lab/ShippingCalculator.java` (Sửa công thức tính cước gói STANDARD từ `weight * 3000` thành `weight * 4000`).
*   **Log minh chứng:**
    ```text
    [ERROR] Failures: 
    [ERROR]   ShippingCalculatorTest.testStandard:12 expected: <15000.0> but was: <20000.0>
    [INFO] 
    [ERROR] Tests run: 3, Failures: 1, Errors: 0, Skipped: 0
    [INFO] BUILD FAILURE
    ```
*   **Nguyên nhân kỹ thuật:** Mã nguồn xử lý nghiệp vụ đã bị thay đổi làm kết quả đầu ra sai lệch so với kỳ vọng (Assertion) được viết trong file Unit Test `ShippingCalculatorTest.java`. Hệ thống CI phát hiện Test case `testStandard` trả về giá trị `20000.0` thay vì `15000.0`, do đó tự động đánh dấu quy trình Build thất bại (Pipeline đỏ) để ngăn chặn đoạn code lỗi này được triển khai.
*   **Cách khắc phục:** Trả lại đúng công thức toán học ban đầu `return weight * 3000;` trong file `ShippingCalculator.java` để đảm bảo logic hoạt động chuẩn xác và vượt qua bài test.