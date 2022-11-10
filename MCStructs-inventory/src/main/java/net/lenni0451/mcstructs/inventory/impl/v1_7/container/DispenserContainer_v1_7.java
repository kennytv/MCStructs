package net.lenni0451.mcstructs.inventory.impl.v1_7.container;

import net.lenni0451.mcstructs.inventory.InventoryHolder;
import net.lenni0451.mcstructs.inventory.Slot;
import net.lenni0451.mcstructs.inventory.impl.v1_7.AContainer_v1_7;
import net.lenni0451.mcstructs.inventory.impl.v1_7.inventory.DispenserInventory_v1_7;
import net.lenni0451.mcstructs.inventory.impl.v1_7.inventory.PlayerInventory_v1_7;
import net.lenni0451.mcstructs.items.AItemStack;

public class DispenserContainer_v1_7<I, S extends AItemStack<I, S>> extends AContainer_v1_7<I, S> {

    private final PlayerInventory_v1_7<I, S> playerInventory;
    private final DispenserInventory_v1_7<I, S> dispenserInventory;

    public DispenserContainer_v1_7(final int windowId, final PlayerInventory_v1_7<I, S> playerInventory) {
        super(windowId);
        this.playerInventory = playerInventory;
        this.dispenserInventory = new DispenserInventory_v1_7<>();

        this.initSlots();
    }

    @Override
    protected void initSlots() {
        for (int i = 0; i < 9; i++) this.addSlot(this.dispenserInventory, i, Slot.acceptAll());
        for (int i = 0; i < 27; i++) this.addSlot(this.playerInventory, 9 + i, Slot.acceptAll());
        for (int i = 0; i < 9; i++) this.addSlot(this.playerInventory, i, Slot.acceptAll());
    }

    public PlayerInventory_v1_7<I, S> getPlayerInventory() {
        return this.playerInventory;
    }

    public DispenserInventory_v1_7<I, S> getDispenserInventory() {
        return this.dispenserInventory;
    }

    @Override
    protected S moveStack(InventoryHolder<PlayerInventory_v1_7<I, S>, I, S> inventoryHolder, int slotId) {
        Slot<PlayerInventory_v1_7<I, S>, I, S> slot = this.getSlot(slotId);
        if (slot == null || slot.getStack() == null) return null;

        S slotStack = slot.getStack();
        S out = slotStack.copy();
        if (slotId <= 8) {
            if (!this.mergeStack(slotStack, 9, 45, true)) return null;
        } else if (!this.mergeStack(slotStack, 0, 9, false)) {
            return null;
        }
        if (slotStack.getCount() == 0) slot.setStack(null);
        else slot.onUpdate();
        if (slotStack.getCount() == out.getCount()) return null;
        slot.onTake(inventoryHolder, slotStack);
        return out;
    }

}
