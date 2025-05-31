# PBL3-Petstore

Đây là ứng dụng Quản lý Cửa hàng Thú cưng được xây dựng bằng JavaFX và Maven.

## Thông tin dự án

*   **IDE:** IntelliJ IDEA (Khuyến nghị)
*   **Loại dự án:** Maven - JavaFX
*   **Phiên bản JDK:** JDK 21 trở lên (dựa trên pom.xml)

## Cấu trúc dự án

*   `src/main/java/com/store/app/petstore/`: Chứa mã nguồn Java chính của ứng dụng.
    *   `Controllers/`: Chứa các controller cho các tệp FXML.
        *   `Admin/`: Controller cho bảng điều khiển quản trị viên.
        *   `Staff/`: Controller cho bảng điều khiển nhân viên.
    *   `DAO/`: Data Access Object để tương tác với cơ sở dữ liệu.
        *   `StatisticDAO/`: DAO dành riêng cho thống kê.
    *   `Models/`: Chứa các mô hình dữ liệu của ứng dụng và các lớp liên quan.
        *   `Entities/`: Các mô hình thực thể cơ sở dữ liệu.
        *   `Records/`: Các bản ghi Java.
        *   `Seeder/`: Các lớp để gieo dữ liệu ban đầu vào cơ sở dữ liệu.
    *   `Sessions/`: Quản lý phiên người dùng.
    *   `Utils/`: Các lớp tiện ích.
        *   `Mappers/`: Các lớp để ánh xạ kết quả truy vấn cơ sở dữ liệu sang mô hình.
*   `src/main/resources/`: Chứa các tài nguyên của ứng dụng.
    *   `Font/`: Các phông chữ tùy chỉnh được sử dụng trong ứng dụng.
    *   `FXML/`: Các tệp FXML định nghĩa bố cục giao diện người dùng.
        *   `Admin/`: Các tệp FXML cho bảng điều khiển quản trị viên.
        *   `Staff/`: Các tệp FXML cho bảng điều khiển nhân viên.
        *   `Statistics/`: Các tệp FXML cho các chế độ xem thống kê.
    *   `Images/`: Các tài sản hình ảnh được sử dụng trong ứng dụng.
        *   `Pet/`: Hình ảnh thú cưng.
        *   `Product/`: Hình ảnh sản phẩm.
        *   `Staff/`: Hình ảnh nhân viên.
        *   `User/`: Hình ảnh người dùng.
    *   `Styles/`: Các tệp CSS để tạo kiểu cho ứng dụng.
        *   `Admin/`: Các tệp CSS cho bảng điều khiển quản trị viên.
        *   `Staff/`: Các tệp CSS cho bảng điều khiển nhân viên.

## Tính năng (Suy luận từ các tệp FXML)

*   Xác thực người dùng (Đăng nhập, Quên mật khẩu)
*   Bảng điều khiển quản trị viên:
    *   Trang tổng quan
    *   Quản lý khách hàng
    *   Quản lý giảm giá
    *   Quản lý kho hàng
    *   Quản lý hóa đơn
    *   Quản lý thú cưng
    *   Quản lý sản phẩm
    *   Quản lý nhân viên
    *   Quản lý người dùng
    *   Thống kê (Bán chạy nhất, Tổng quan, Doanh thu)
*   Bảng điều khiển nhân viên:
    *   Lịch sử hóa đơn
    *   Thông tin khách hàng
    *   Danh sách mặt hàng
    *   Quản lý đơn hàng
    *   Xử lý thanh toán
    *   Thông tin cá nhân
    *   Xem chi tiết mặt hàng thú cưng và sản phẩm
    *   Trang tổng quan

## Cơ sở dữ liệu

Ứng dụng sử dụng trình điều khiển JDBC cho Oracle và MySQL, cho thấy hỗ trợ cho cả hai hệ quản trị cơ sở dữ liệu này. Chi tiết kết nối cơ sở dữ liệu có thể được cấu hình thông qua các biến môi trường sử dụng `dotenv-java`.

## Xây dựng và chạy ứng dụng

1.  **Điều kiện tiên quyết:**
    *   Cài đặt Java Development Kit (JDK) phiên bản 21 trở lên.
    *   Cài đặt Apache Maven.
    *   Thiết lập cơ sở dữ liệu Oracle hoặc MySQL và cập nhật chi tiết kết nối (có thể trong tệp `.env` dựa trên dependency `dotenv-java`).
2.  **Clone repository:**
    ```bash
    git clone <repository_url>
    cd PBL3-Petstore
    ```
3.  **Xây dựng dự án bằng Maven:**
    ```bash
    mvn clean package
    ```
4.  **Chạy ứng dụng:**
    ```bash
    mvn clean javafx:run
    ```

## Các Dependency

Dự án sử dụng các dependency chính sau:

*   JavaFX (cho giao diện người dùng đồ họa)
*   ControlsFX, FormsFX, Ikonli-javafx, BootstrapFX, TilesFX (cho các điều khiển UI nâng cao và tạo kiểu)
*   Trình điều khiển JDBC cho Oracle và MySQL (để kết nối cơ sở dữ liệu)
*   jbcrypt (để băm mật khẩu)
*   dotenv-java (để tải biến môi trường)
