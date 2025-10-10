# ToDo App - Ứng dụng Quản lý Công việc

Một ứng dụng To-Do List đầy đủ tính năng được xây dựng cho nền tảng Android. Ứng dụng cho phép người dùng quản lý các công việc hàng ngày một cách hiệu quả, với dữ liệu được đồng bộ hóa real-time giữa các thiết bị thông qua Firebase và lưu trữ cục bộ bằng Room Database, đảm bảo có thể sử dụng ngay cả khi không có mạng.

---

## 📸 Ảnh chụp màn hình

| Màn hình Đăng nhập | Màn hình Chính | Menu Lọc & Sắp xếp |
|:------------------:|:--------------:|:------------------:|
| ![Màn hình Đăng nhập](https://github.com/user-attachments/assets/c1cc75f0-32cd-47a0-a8ea-f1c5443e270a) | ![Màn hình Chính](https://github.com/user-attachments/assets/6d5887b3-a3eb-45c7-9f05-2b054a910c32) | ![Menu Lọc & Sắp xếp](https://github.com/user-attachments/assets/50295e4b-cc13-420f-93d3-0336171b2586) |

---

## ✨ Tính năng chính

### 🔐 Xác thực người dùng
- Đăng ký và đăng nhập bằng Email/Password
- Đăng nhập bằng tài khoản Google (sử dụng Credential Manager hiện đại)
- Tự động đăng nhập cho các phiên sau
- Đăng xuất an toàn, xóa thông tin đăng nhập Google đã lưu

### 📝 Quản lý công việc (CRUD)
- Thêm công việc mới với tiêu đề, mô tả và thời hạn (deadline)
- Xem danh sách công việc chi tiết
- Sửa lại thông tin của công việc đã có
- Xóa công việc (có hộp thoại xác nhận)
- Đánh dấu công việc đã hoàn thành hoặc chưa hoàn thành

### 🔍 Lọc, Tìm kiếm và Sắp xếp
- Tìm kiếm công việc theo tiêu đề (real-time)
- Lọc theo trạng thái: Tất cả, Đã hoàn thành, Chưa làm
- Lọc theo khoảng ngày (từ ngày - đến ngày)
- Sắp xếp danh sách theo ngày (mới nhất/cũ nhất) hoặc theo tên (A-Z)

### 🔄 Đồng bộ hóa & Offline-first
- Sử dụng Firebase Realtime Database để đồng bộ dữ liệu giữa các thiết bị
- Sử dụng Room Database làm "Single Source of Truth", cho phép ứng dụng hoạt động mượt mà ngay cả khi offline

### 🔔 Thông báo nhắc nhở
- Đặt lịch thông báo (notification) cho các công việc có thời hạn bằng AlarmManager
- Tự động hủy và đặt lại thông báo khi người dùng cập nhật công việc

### 🎨 Giao diện người dùng
- Thiết kế theo phong cách Material Design
- Sử dụng Navigation Drawer để chứa các chức năng nâng cao, giúp giao diện chính luôn gọn gàng
- Hiển thị thông tin người dùng (tên, avatar) trong menu

---

## 🏗️ Kiến trúc và Công nghệ sử dụng

### Kiến trúc
- **MVVM (Model-View-ViewModel)**: Tách biệt logic giao diện (View), logic nghiệp vụ (ViewModel) và dữ liệu (Model)
- **Repository Pattern**: Cung cấp một lớp trừu tượng để quản lý các nguồn dữ liệu (local và remote)

### Công nghệ sử dụng

**Ngôn ngữ & Lập trình bất đồng bộ**
- Kotlin
- Kotlin Coroutines & LifecycleScope

**Android Jetpack**
- **ViewModel**: Quản lý trạng thái và logic liên quan đến UI
- **LiveData & MediatorLiveData**: Tạo các luồng dữ liệu phản ứng
- **Room**: Lưu trữ dữ liệu cục bộ
- **Navigation Component**: Quản lý luồng di chuyển giữa các màn hình
- **ViewBinding**: Tương tác với các view một cách an toàn

**Firebase**
- **Firebase Authentication**: Xác thực người dùng
- **Firebase Realtime Database**: Đồng bộ hóa dữ liệu

**Google Sign-In**
- **Credential Manager**: API hiện đại để xử lý đăng nhập bằng Google

**UI Components**
- Material Design Components (Card, Chip, Button,...)
- RecyclerView: Hiển thị danh sách
- Glide: Tải và hiển thị ảnh
- CircleImageView: Hiển thị avatar dạng tròn

---

## 🌳 Cấu trúc thư mục dự án

```
com.example.todo6
├── data
│   ├── dao
│   │   └── TaskDAO.kt
│   ├── database
│   │   └── TaskDatabase.kt
│   ├── model
│   │   └── Task.kt
│   └── repository
│       └── TaskRepository.kt
├── ui
│   ├── fragments
│   │   ├── HomeFragment.kt
│   │   ├── SignInFragment.kt
│   │   ├── SignUpFragment.kt
│   │   └── SplashFragment.kt
│   ├── task
│   │   ├── AddTaskPopupFragment.kt
│   │   ├── EditTaskFragment.kt
│   │   ├── TaskAdapter.kt
│   │   ├── TaskDetailPopupFragment.kt
│   │   ├── TaskViewModel.kt
│   │   └── TaskViewModelFactory.kt
│   └── MainActivity.kt
└── utils
    └── NotificationReceiver.kt
```

---

## 🚀 Hướng dẫn Cài đặt

### 1. Clone repository

```bash
git clone https://github.com/XuanQuyet-88/ToDo-App.git
```

### 2. Mở dự án bằng Android Studio

### 3. Cấu hình Firebase

1. Truy cập [Firebase Console](https://console.firebase.google.com/) và tạo một dự án mới
2. Trong mục **Authentication**, vào tab **Sign-in method** và bật **Email/Password** và **Google**
3. Trong mục **Realtime Database**, tạo một database mới ở chế độ test
4. Trong **Project Settings**, thêm một ứng dụng Android với package name là `com.example.todo6`
5. Làm theo hướng dẫn để lấy mã **SHA-1** (debug) từ máy của bạn và thêm vào phần cấu hình ứng dụng Android trong Firebase
6. Tải file `google-services.json` từ Firebase và đặt nó vào thư mục `app/` của dự án

### 4. Build và chạy ứng dụng

---

## 👨‍💻 Tác giả

**Nguyễn Xuân Quyết** - [GitHub](https://github.com/XuanQuyet-88)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

⭐ Nếu bạn thấy dự án này hữu ích, hãy cho một ngôi sao nhé!
