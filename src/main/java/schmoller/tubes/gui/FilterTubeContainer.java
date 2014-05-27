package schmoller.tubes.gui;

import schmoller.tubes.api.gui.ExtContainer;
import schmoller.tubes.api.gui.FakeSlot;
import schmoller.tubes.api.gui.GuiColorButton;
import schmoller.tubes.api.gui.GuiEnumButton;
import schmoller.tubes.api.gui.GuiEnumButton.INameCallback;
import schmoller.tubes.api.interfaces.IFilter;
import schmoller.tubes.types.FilterTube;
import schmoller.tubes.types.FilterTube.Comparison;
import schmoller.tubes.types.FilterTube.Mode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class FilterTubeContainer extends ExtContainer
{
	public FilterTubeContainer(final FilterTube tube, EntityPlayer player)
	{
		for(int i = 0; i < 2; ++i)
		{
			for(int j = 0; j < 8; ++j)
				addSlotToContainer(new FilterSlot(tube, j + (i * 8), 8 + j * 18, 20 + i * 18));
		}
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 72 + i * 18));
        }

        for (int i = 0; i < 9; ++i)
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 130));
        
        
        INameCallback<Mode> modeName = new INameCallback<Mode>()
		{
        	@Override
        	public String getNameFor( Mode e )
        	{
        		return StatCollector.translateToLocalFormatted("gui.filtertube.modestring", StatCollector.translateToLocal(String.format("gui.filtertube.mode.%s", e.name())));
        	}
		};
		
		INameCallback<Comparison> comparisonName = new INameCallback<Comparison>()
		{
        	@Override
        	public String getNameFor( Comparison e )
        	{
        		Mode mode = tube.getProperty(FilterTube.PROP_MODE);
        		return StatCollector.translateToLocalFormatted(String.format("gui.filtertube.size.%s", e.name()), StatCollector.translateToLocal(String.format("gui.filtertube.mode.%s", mode.name())));
        	}
		};
        
		addButtonToContainer(new GuiEnumButton<Mode>(tube, FilterTube.PROP_MODE, Mode.class, 153, 19, 176, 0, modeName));
        addButtonToContainer(new GuiEnumButton<Comparison>(tube, FilterTube.PROP_COMPARISON, Comparison.class, 153, 35, 190, 0, comparisonName));
        addButtonToContainer(new GuiColorButton(tube, FilterTube.PROP_COLOR, 153, 51));
	}
	
	@Override
	public boolean canInteractWith( EntityPlayer entityplayer )
	{
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot( EntityPlayer player, int slotId )
	{
		ItemStack ret = null;
        Slot slot = (Slot)inventorySlots.get(slotId);

        if (slotId >= 16 && slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ret = stack.copy();

            if(slotId >= 43 && slotId < 52) // From hotbar
            {
                if (!mergeItemStack(stack, 16, 43, false)) // To main inventory
                	return null;
            }
            else // From main inventory
            {
            	if (!mergeItemStack(stack, 43, 52, false)) // To hotbar
                	return null;
            }

            if (stack.stackSize == 0)
                slot.putStack((ItemStack)null);
            else
                slot.onSlotChanged();

            if (ret.stackSize == stack.stackSize)
                return null;

            slot.onPickupFromSlot(player, ret);
        }

        return ret;
	}
	
	public class FilterSlot extends FakeSlot
	{
		private FilterTube mTube;
		private int mIndex;
		
		public FilterSlot(FilterTube tube, int index, int x, int y)
		{
			super(tube.getFilter(index), x, y);
			mTube = tube;
			mIndex = index;
		}
		
		@Override
		protected void setValue( IFilter item )
		{
			mTube.setFilter(mIndex, item);
		}
		
		@Override
		public boolean shouldRespectSizes()
		{
			return true;
		}
		
		@Override
		public boolean filterNeedsPayload() { return false; }
	}
}
