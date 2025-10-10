# ToDo App - Ứng dụng Ghi Chú Công Việc

![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Platform](https://img.shields.io/badge/Platform-Android-brightgreen.svg)
![API](https://img.shields.io/badge/API-24%2B-orange.svg)

Đây là một ứng dụng To-Do List đầy đủ tính năng được xây dựng cho nền tảng Android. Ứng dụng cho phép người dùng quản lý các công việc hàng ngày một cách hiệu quả, với dữ liệu được đồng bộ hóa real-time giữa các thiết bị thông qua Firebase và lưu trữ cục bộ bằng Room Database, đảm bảo có thể sử dụng ngay cả khi không có mạng.

## Ảnh chụp màn hình

| Màn hình Đăng nhập | Màn hình Chính | Menu Lọc & Sắp xếp |
| :---: | :---: | :---: |
| ![Ảnh màn hình Đăng nhập](![sigin_screen_todo](https://github.com/user-attachments/assets/d037a9ea-8ce9-466b-b10c-976a947a2466)
) | ![Ảnh màn hình Chính](![main_act_todo](https://github.com/user-attachments/assets/46f62036-75c6-45f8-9921-2c9611896bba)
) | ![Ảnh màn hình Menu](![filter_sort_screen_todo_app](https://github.com/user-attachments/assets/ea629a06-ca43-4834-bf0d-4a8105f34049)
g) |
*(Lưu ý: Bạn hãy thay thế các link ảnh trên bằng ảnh chụp màn hình thực tế của ứng dụng)*

## Tính năng chính

* **Xác thực người dùng:**
    * Đăng ký và đăng nhập bằng Email/Password.
    * Đăng nhập bằng tài khoản Google (sử dụng **Credential Manager** hiện đại).
    * Tự động đăng nhập cho các phiên sau.
    * Đăng xuất an toàn, xóa thông tin đăng nhập Google đã lưu.

* **Quản lý công việc (CRUD):**
    * **Thêm** công việc mới với tiêu đề, mô tả và thời hạn (deadline).
    * **Xem** danh sách công việc.
    * **Sửa** lại thông tin của công việc đã có.
    * **Xóa** công việc (có hộp thoại xác nhận).
    * **Đánh dấu** công việc đã hoàn thành hoặc chưa hoàn thành.

* **Lọc và Tìm kiếm:**
    * **Tìm kiếm** công việc theo tiêu đề (real-time).
    * **Lọc** theo trạng thái: Tất cả, Đã hoàn thành, Chưa làm.
    * **Lọc** theo khoảng ngày (từ ngày - đến ngày).

* **Sắp xếp:**
    * Sắp xếp danh sách công việc theo **ngày** (mới nhất hoặc cũ nhất).
    * Sắp xếp danh sách công việc theo **tên** (A-Z).

* **Đồng bộ hóa dữ liệu:**
    * Sử dụng **Firebase Realtime Database** để đồng bộ dữ liệu giữa các thiết bị.
    * Sử dụng **Room Database** để lưu trữ dữ liệu cục bộ, hoạt động như một "Single Source of Truth", cho phép ứng dụng hoạt động offline.

* **Thông báo nhắc nhở:**
    * Đặt lịch thông báo (notification) cho các công việc có thời hạn bằng `AlarmManager`.
    * Hủy và đặt lại thông báo khi người dùng cập nhật công việc.

* **Giao diện người dùng:**
    * Thiết kế theo phong cách Material Design.
    * Sử dụng `Navigation Drawer` để chứa các chức năng nâng cao (lọc, sắp xếp, thông tin người dùng) giúp giao diện chính luôn gọn gàng.
    * Hiển thị thông tin người dùng (tên, avatar) trong menu.

## Kiến trúc và Công nghệ sử dụng

* **Kiến trúc:**
    * **MVVM (Model-View-ViewModel)**: Tách biệt logic giao diện (View), logic nghiệp vụ (ViewModel) và dữ liệu (Model), giúp code dễ dàng bảo trì và mở rộng.
    * **Repository Pattern**: Cung cấp một lớp trừu tượng để quản lý các nguồn dữ liệu (local và remote).

* **Công nghệ sử dụng:**
    * **Ngôn ngữ**: [Kotlin](https://kotlinlang.org/)
    * **Lập trình bất đồng bộ**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
    * **Android Jetpack**:
        * `ViewModel`: Quản lý trạng thái và logic liên quan đến UI.
        * `LiveData` & `MediatorLiveData`: Tạo các luồng dữ liệu phản ứng.
        * `Room`: Lưu trữ dữ liệu cục bộ.
        * `Navigation Component`: Quản lý luồng di chuyển giữa các màn hình.
        * `ViewBinding`: Tương tác với các view một cách an toàn.
    * **Firebase**:
        * `Firebase Authentication`: Xác thực người dùng.
        * `Firebase Realtime Database`: Đồng bộ hóa dữ liệu.
    * **Google Sign-In**:
        * `Credential Manager`: API hiện đại để xử lý đăng nhập bằng Google.
    * **UI**:
        * `Material Design Components`: (Card, Chip, Button,...)
        * `RecyclerView`: Hiển thị danh sách.
        * [Glide](https://github.com/bumptech/glide): Tải và hiển thị ảnh.
        * [CircleImageView](https://github.com/hdodenhof/CircleImageView): Hiển thị avatar dạng tròn.

## Hướng dẫn Cài đặt

1.  **Clone a copy of the repository:**
    ```bash
    git clone [https://github.com/your-username/your-repository-name.git](https://github.com/your-username/your-repository-name.git)
    ```

2.  **Mở dự án bằng Android Studio.**

3.  **Cấu hình Firebase:**
    * Truy cập [Firebase Console](https://console.firebase.google.com/) và tạo một dự án mới.
    * Trong mục **Authentication**, vào tab **Sign-in method** và bật **Email/Password** và **Google**.
    * Trong mục **Realtime Database**, tạo một database mới ở chế độ test.
    * Trong **Project Settings**, thêm một ứng dụng Android với package name là `com.example.todo6`.
    * Làm theo hướng dẫn để lấy **mã SHA-1** (debug) từ máy của bạn và thêm vào phần cấu hình ứng dụng Android trong Firebase.
    * Tải file `google-services.json` từ Firebase và đặt nó vào thư mục `app/` của dự án.

4.  **Build và chạy ứng dụng.**

## Tác giả

* [Tên của bạn] - [Link GitHub của bạn]
