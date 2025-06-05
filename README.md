# PBL3-Petstore

- IDE : Intellij
- Project : Maven - Javafx 
- JDK version : JDK 23


## Giải thích cấu trúc thư mục 
### Controllers 
- .... 

### Model 
#### Entities 
- Chứa các model từ database 
#### Seeder 
- Chứa các file tạo dữ liệu 
- Sử dụng để test dữ liệu 

### Repository
- Đọc dữ liệu từ database 
- Trong controller. 
- Các file tương ứng với mỗi file trong entites 

### Service
- UserService : Các truy vấn liên quan đến 
- AuthService : Đăng nhập, đăng xuất User

### Session
- Lưu trữ session ảo hiện tại

### Utils 
#### Mapper 
- Chuyển đổi dữ liệu truy vấn từ Database sang kiểu dữ liệu tương ứng
- Param (resultset)


