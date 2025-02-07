package net.lenni0451.mcstructs.inventory.impl.v1_7.container;

import net.lenni0451.mcstructs.inventory.InventoryHolder;
import net.lenni0451.mcstructs.inventory.Slot;
import net.lenni0451.mcstructs.inventory.impl.v1_7.AContainer_v1_7;
import net.lenni0451.mcstructs.inventory.impl.v1_7.inventory.PlayerInventory_v1_7;
import net.lenni0451.mcstructs.inventory.impl.v1_7.inventory.SimpleInventory_v1_7;
import net.lenni0451.mcstructs.items.AItemStack;

public class ChestContainer_v1_7<T extends PlayerInventory_v1_7<I, S>, I, S extends AItemStack<I, S>> extends AContainer_v1_7<T, I, S> {

    private final T playerInventory;
    private final SimpleInventory_v1_7<I, S> chestInventory;
    private final int rowCount;
    private final int rowSlotCount;

    public ChestContainer_v1_7(final int windowId, final T playerInventory, final int size) {
        super(windowId);
        this.playerInventory = playerInventory;
        this.chestInventory = new SimpleInventory_v1_7<>(size);
        this.rowCount = this.chestInventory.getSize() / 9;
        this.rowSlotCount = this.rowCount * 9;

        this.initSlots();
    }

    @Override
    protected void initSlots() {
        for (int i = 0; i < this.rowSlotCount; i++) this.addSlot(this.chestInventory, i, Slot.acceptAll());
        for (int i = 0; i < 27; i++) this.addSlot(this.playerInventory, 9 + i, Slot.acceptAll());
        for (int i = 0; i < 9; i++) this.addSlot(this.playerInventory, i, Slot.acceptAll());
    }

    public T getPlayerInventory() {
        return this.playerInventory;
    }

    public SimpleInventory_v1_7<I, S> getChestInventory() {
        return this.chestInventory;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getRowSlotCount() {
        return this.rowSlotCount;
    }

    @Override
    protected S moveStack(InventoryHolder<T, I, S> inventoryHolder, int slotId) {
        Slot<T, I, S> slot = this.getSlot(slotId);
        if (slot == null || slot.getStack() == null) return null;

        S slotStack = slot.getStack();
        S out = slotStack.copy();
        if (slotId < this.rowSlotCount) {
            if (!this.mergeStack(slotStack, this.rowSlotCount, this.getSlotCount(), true)) return null;
        } else if (!this.mergeStack(slotStack, 0, this.rowSlotCount, false)) return null;
        if (slotStack.getCount() == 0) slot.setStack(null);
        else slot.onUpdate();
        return out;
    }

}
