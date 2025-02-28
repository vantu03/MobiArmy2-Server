package mobiarmy.server;

import java.util.ArrayList;
import static mobiarmy.server.Text.__;

/**
 *
 * @author Văn Tú
 */
public class Select {
    
    public User user;
    public ArrayList<Element> elements;
    
    public Select(User user) {
        this.user = user;
        this.elements = new ArrayList<>();
    }
    
    public void reset() {
        this.elements.clear();
    }
    
    public void addElement(int type, int id, int quntity) {
        this.elements.add(new Element(type, id, quntity));
    }
    
    public int sizeOfType(int type) {
        int size = 0;
        for (Element element : this.elements) {
            if (element.type == type) {
                size++;
            }
        }
        return size;
    }
    
    public Element getFirstOfType(int type) {
        for (Element element : this.elements) {
            if (element.type == type) {
                return element;
            }
        }
        return null;
    }
    
    public class Element {
        
        public int type;
        public int id;
        public int num;

        public Element(int type, int id, int num) {
            this.type = type;
            this.id = id;
            this.num = num;
        }
    }
    
    public void make() {
        if (this.sizeOfType(0) == 1 && this.sizeOfType(1) == 1 && this.getFirstOfType(1).id < 50) {
            this.user.getConfirm().addConfirm(0, __("Bạn có muốn gắn ngọc vào trang bị?."), new int[]{this.getFirstOfType(0).id, this.getFirstOfType(1).id});
        } else if(this.sizeOfType(0) == 0 && this.sizeOfType(1) == 1 && this.getFirstOfType(1).id < 50 && this.getFirstOfType(1).num > 0) {
            Element element = getFirstOfType(1);
            LinhTinh item = this.user.getLinhTinh(this.getFirstOfType(1).id);
            if (item != null) {
                if (element.num > item.num) {
                    element.num = item.num;
                }
                if (element.num % 5 == 0 && (item.id + 1) % 10 != 0) {
                    this.user.getConfirm().addConfirm(4, String.format(
                            __("Bạn có muốn kết hợp %d ngọc này thành %d viên ngọc cao cấp hơn? Tỉ lệ thành công là %d%%"),
                            element.num,
                            element.num / 5,
                            100 - ((item.id + 1) % 10) * 10
                    ), new int[]{item.id, element.num});
                } else {
                    this.user.getConfirm().addConfirm(3, String.format(
                            __("Bạn có muốn bán %d vật phẩm này với giá %d xu không?"),
                            element.num, item.xu / 2 * element.num
                    ), new int[]{item.id, element.num});
                }
            }
        } else {
            if (this.user.session != null) {
                this.user.session.sessionHandler.log("Không thể kết hợp.");
            }
        }
    }
}
