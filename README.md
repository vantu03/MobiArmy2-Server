# 📌 MobiArmy Server - Java + MySQL

👉 Đây là server **MobiArmy** được viết bằng **Java** và sử dụng **MySQL** để quản lý dữ liệu. Server hỗ trợ đầy đủ các tính năng kết nối, quản lý người chơi, và xử lý dữ liệu trong game.

## 🔹 Giới thiệu
- **MobiArmy Server** được xây dựng nhằm tái tạo lại hệ thống server của game MobiArmy 2.
- Hỗ trợ đầy đủ các chức năng của game như xử lý kết nối, quản lý nhân vật, phòng đấu, vũ khí, và cơ chế chiến đấu.
- Server được viết bằng **Java 8+** và sử dụng **MySQL** để lưu trữ dữ liệu.

### 🛠 Công nghệ sử dụng
- **Java 8+**
- **MySQL** (Cơ sở dữ liệu lưu trữ thông tin nhân vật, trận đấu,...)
- **NetBeans/IntelliJ IDEA** (Môi trường phát triển khuyến nghị)

## 📸 Hình ảnh minh họa
Dưới đây là một số hình ảnh về hệ thống server đang hoạt động:

### 🔹 Giao diện quản lý server
![Giao diện quản lý server](src/Screenshot_2025-02-28_085342.png)

### 🔹 Cấu hình kết nối MySQL
![Cấu hình MySQL](src/Screenshot_2025-02-28_085448.png)

### 🔹 Xử lý bot trong game
![Xử lý bot](src/Screenshot_2025-02-28_085458.png)

### 🔹 Server đang hoạt động
![Server đang hoạt động](src/Screenshot_2025-02-28_085510.png)

## 💡 Hướng dẫn cài đặt & chạy server

1️⃣ **Clone repository:**
```sh
git clone https://github.com/vantu03/MobiArmy2-Server.git
```

2️⃣ **Cấu hình MySQL:**
- Tạo database với tên `army`
- Cập nhật thông tin kết nối trong tệp `DBManager.java`
```java
DBManager dbManager = new DBManager("jdbc:mysql://localhost:3306/army", "root", "");
```

3️⃣ **Chạy server:**
- Mở dự án bằng **NetBeans** hoặc **IntelliJ IDEA**
- Chạy file `MobiArmy.java` để khởi động server

4️⃣ **Kết nối client:**
- Sau khi server chạy, client có thể kết nối bằng cách nhập địa chỉ server và port
- Mặc định server chạy trên **port 8122**

🔗 **Tải Client tại đây:** [MobiArmy2 Client](https://github.com/vantu03/MobiArmy2-Client)

---

💚 Nếu bạn có bất kỳ góp ý nào, hãy mở **Issues** hoặc tạo **Pull Request** để cải thiện dự án! 🚀

---

