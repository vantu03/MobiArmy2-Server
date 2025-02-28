package mobiarmy.server;

import mobiarmy.Util;
import static mobiarmy.server.Text.__;

/**
 *
 * @author Văn Tú
 */
public class Confirm {
    
    public User user;
    public int action;
    public String str;
    public Object object;
    
    public Confirm(User user) {
        this.user = user;
        this.action = -1;
    }
    
    public void addConfirm(int action, String str, Object object) {
        this.action = action;
        this.str = str;
        this.object = object;
        if (this.user.session != null) {
            this.user.session.sessionHandler.confirm(str);
        }
    }
    
    public void addConfirm2(int action, String str, Object object) {
        this.action = action;
        this.str = str;
        this.object = object;
        this.user.session.sessionHandler.confirm2(str);
    }
    
    public void confirm() {
        switch (this.action) {
            case 0 -> {
                int array[] = (int[])this.object;
                Equip equip = this.user.getEquip(array[0]);
                LinhTinh item = this.user.getLinhTinh(array[1]);
                if (equip != null && item != null) {
                    if (equip.slot() > 0) {
                        equip.slot[equip.slot()-1] = item.id;
                        for (int i = 0; i < item.ability.length; i++) {
                            equip.inv_ability[i] += item.ability[i];
                        }
                        if (this.user.session != null) {
                            this.user.session.sessionHandler.updateEquip(equip);
                        }
                        this.user.addLinhTinh(item.id, -1);
                        if (this.user.session != null) {
                            this.user.session.sessionHandler.log(__("Chúc mừng bạn đã kết hợp thành công."));
                        }
                    } else {
                        if (this.user.session != null) {
                            this.user.session.sessionHandler.log(__("Trang bị đã hết số lần kết hợp."));
                        }
                    }
                }
            }
            case 1 -> {
                int dbKey = (int)this.object;
                Equip equip = this.user.getEquip(dbKey);
                if (equip != null && equip.slot.length - equip.slot() > 0) {
                    int price = (int) (equip.priceSlot() * 0.25);
                    if (price <= this.user.xu) {
                        this.user.addXu(-price, true);
                        for (int i = 0; i < equip.slot.length; i++) {
                            if (equip.slot[i] != -1) {
                                LinhTinh item = LinhTinh.get(equip.slot[i]);
                                if (item != null) {
                                    this.user.addLinhTinh(item.id, 1);
                                    for (int j = 0; j < item.ability.length; j++) {
                                        equip.inv_ability[j] -= item.ability[j];
                                    }
                                }
                            }
                            equip.slot[i] = -1;
                        }
                        if (this.user.session != null) {
                            this.user.session.sessionHandler.updateEquip(equip);
                            this.user.session.sessionHandler.log(__("Tháo lấy ngọc thành công."));
                        }
                    } else {
                        if (this.user.session != null) {
                            this.user.session.sessionHandler.log(__("Bạn không đủ xu để tháo."));
                        }
                    }
                }
            }
            case 2 -> {
                int[] dbKey = (int[]) this.object;
                int price = 0;
                for (int i = 0; i < dbKey.length; i++) {
                    Equip equip = this.user.getEquip(dbKey[i]);
                    if (equip != null && !equip.isUse) {
                        price += equip.calculatePrice();
                        this.user.removeEquip(equip);
                    }
                }
                this.user.addXu(price, true);
                if (this.user.session != null) {
                    this.user.session.sessionHandler.log(String.format(__("Bán thành công, thu được %d xu."), price));
                }
            }
            case 3 -> {
                int[] array = (int[]) this.object;
                LinhTinh item = this.user.getLinhTinh(array[0]);
                if (item != null) {
                    if (array[1] > item.num) {
                        array[1] = item.num;
                    }
                    this.user.addLinhTinh(item.id, -array[1]);
                    this.user.addXu(item.xu / 2 * array[1], true);
                    if (this.user.session != null) {
                        this.user.session.sessionHandler.log(__("Giao dịch thành công. Xin cảm ơn."));
                    }
                }
            }
            case 4 -> {
                int[] array = (int[]) this.object;
                LinhTinh item = this.user.getLinhTinh(array[0]);
                if (item != null) {
                    if (array[1] > item.num) {
                        array[1] = item.num / 5;
                    } else {
                        array[1] /= 5;
                    }
                    int count = 0;
                    for (int i = 0; i < array[1]; i++) {
                        boolean flag = Util.nextInt(1, 100) <= 100 - ((item.id + 1) % 10) * 10;
                        if (flag) {
                            count++;
                            this.user.addLinhTinh(item.id, -5);
                            this.user.addLinhTinh(item.id + 1, 1);
                            if ((item.id + 1) % 10 == 7) {
                                this.user.addMission(9, 1);
                            }
                            if ((item.id + 1) % 10 == 8) {
                                this.user.addMission(10, 1);
                            }
                            if ((item.id + 1) % 10 == 9) {
                                this.user.addMission(11, 1);
                            }
                        } else {
                            this.user.addLinhTinh(item.id, -1);
                        }
                    }
                    if (count > 0) {
                        if (this.user.session != null) {
                            this.user.session.sessionHandler.log(String.format(__("Chúc mừng, bạn đã nhập thành công %d viên %s"), count, LinhTinh.get(item.id + 1).name));
                        }
                    } else {
                        if (this.user.session != null) {
                            this.user.session.sessionHandler.log(__("Chúc bạn may mắn lần sau."));
                        }
                    }
                }
            }
        }
    }
    
}
