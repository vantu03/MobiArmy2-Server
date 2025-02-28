package mobiarmy.war;

import java.util.ArrayList;

public class PathSimulator {

    private short startX, startY;  // Tọa độ ban đầu
    public short targetX;  // Tọa độ đích
    public short targetY;  // Tọa độ đích
    private MapData mapData;  // Dữ liệu bản đồ
    public ArrayList<short[]> pathFrames = new ArrayList<>();  // Lưu lại từng bước di chuyển

    public PathSimulator(int startX, int startY, int targetX, int targetY, MapData mapData) {
        this.startX = (short) startX;
        this.startY = (short) startY;
        this.targetX = (short) targetX;
        this.targetY = (short) targetY;
        this.mapData = mapData;
    }

    public boolean simulate() {
        short currentX = startX;
        short currentY = startY;
        pathFrames.clear();  // Xóa dữ liệu cũ

        for (int i = 0; (currentX != targetX || currentY != targetY) && i < 60; i++) {
            // Lưu lại bước hiện tại
            pathFrames.add(new short[]{currentX, currentY});

            // Tính toán bước đi tiếp theo
            int prevX = currentX;
            int prevY = currentY;

            if (this.targetX < currentX) {
                currentX--;
            } else if (this.targetX > currentX) {
                currentX++;
            }

            // Kiểm tra va chạm và cập nhật Y nếu cần
            if (mapData.isCollisionMap(currentX, currentY - 5)) {
                currentY--;  // Nếu có va chạm thì giảm Y
            } else {
                // Nếu không có va chạm, mô phỏng rơi tự do
                currentY = simulateFall(currentX, currentY);
            }

            // Kiểm tra nếu đã ra khỏi bản đồ
            if (currentY > mapData.height + 100) {
                return false;
            }

            // Nếu không thể di chuyển được nữa, dừng lại
            if (prevX == currentX && prevY == currentY) {
                return false;
            }
        }

        // Lưu bước cuối cùng
        pathFrames.add(new short[]{currentX, currentY});
        return true;
    }

    // Hàm mô phỏng rơi tự do
    private short simulateFall(short x, short y) {
        while (y < mapData.height + 200) {
            if (mapData.isCollisionMap(x, y)) {
                return y;  // Nếu có va chạm, trả về tọa độ Y hiện tại
            }
            y++;  // Tiếp tục rơi xuống
        }
        return y;  // Nếu vượt ra khỏi bản đồ, trả về tọa độ cuối cùng
    }
}
