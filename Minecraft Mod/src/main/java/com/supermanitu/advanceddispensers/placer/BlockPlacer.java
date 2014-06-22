package com.supermanitu.advanceddispensers.placer;

import java.util.Random;

import com.supermanitu.advanceddispensers.autocrafting.TileEntityAutoCrafting;
import com.supermanitu.advanceddispensers.main.AdvancedDispensersMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class BlockPlacer extends BlockContainer
{
	private int tickRate;
	
	private PlacerTextureHelper textureHelper;
	private Random rand = new Random();
	
	public BlockPlacer(int tickRate) 
	{
		super(Material.wood);
		this.tickRate = tickRate;
		this.setCreativeTab(AdvancedDispensersMod.advancedDispensersTab);
		this.setHardness(2f);
		this.setBlockName("blockPlacer");
		this.setStepSound(soundTypeWood);
		
		this.textureHelper = new PlacerTextureHelper();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		textureHelper.registerBlockIcons(register);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta)
	{
		return textureHelper.getIcon(side, meta);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType()
	{
		return AdvancedDispensersMod.renderID;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
        	TileEntity tileentity = world.getTileEntity(x, y, z);

            if (tileentity != null && !player.isSneaking())
            {
            	player.openGui(AdvancedDispensersMod.instance, 0, world, x, y, z);
            }
            else
            {
            	return false;
            }

            return true;
        }
    }
	
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
        TileEntityPlacer tileEntityPlacer = (TileEntityPlacer)world.getTileEntity(x, y, z);

        if (tileEntityPlacer != null)
        {
            for (int i1 = 0; i1 < tileEntityPlacer.getSizeInventory(); ++i1)
            {
                ItemStack itemstack = tileEntityPlacer.getStackInSlot(i1);

                if (itemstack != null)
                {
                    float f = this.rand.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityitem;

                    for (float f2 = this.rand.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem))
                    {
                        int j1 = this.rand.nextInt(21) + 10;

                        if (j1 > itemstack.stackSize)
                        {
                            j1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j1;
                        entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
                        float f3 = 0.05F;
                        entityitem.motionX = (double)((float)this.rand.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.rand.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.rand.nextGaussian() * f3);

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }
                    }
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, p_149749_6_);
    }
	
	@Override
	public boolean hasComparatorInputOverride()
	{
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int meta)
	{
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(x, y, z));
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack itemStack)
	{
		super.onBlockPlacedBy(world, x, y, z, livingBase, itemStack);
		setDefaultDirection(world, x, y, z, livingBase);
	}
	
	@Override
	public int tickRate(World world)
	{
		return tickRate;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if(!world.isRemote)
		{
			this.placeBlockInFront(world, x, y, z);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
        int l = world.getBlockMetadata(x, y, z);
        boolean flag1 = (l & 8) != 0;

        if (flag && !flag1)
        {
        	world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
        	world.setBlockMetadataWithNotify(x, y, z, l | 8, 4);
        }
        else if (!flag && flag1)
        {
        	world.setBlockMetadataWithNotify(x, y, z, l & -9, 4);
        }
    }

	@Override
	public TileEntity createNewTileEntity(World world, int var2)
	{
		return new TileEntityPlacer();
	}
	
	public Object[] getRecipe()
	{
		return new Object[]{"XCX", "CSC", "FCF", 'X', Blocks.planks, 'C', Items.redstone, 'S', Blocks.dispenser, 'F', Blocks.stone};
	}
	
	private void placeBlockInFront(World world, int x, int y, int z)
	{
		TileEntityPlacer tileEntity = (TileEntityPlacer) world.getTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		
		int i = getI(meta, x);
		int j = getJ(meta, y);
		int k = getK(meta, z);
		
		int slot = getFirstSlot(tileEntity, world, i, j, k);
		
		if(slot == -1) return;
		
		Block block = null;
		
		if(tileEntity.getStackInSlot(slot).getItem() instanceof IPlantable)
		{
			block = ((IPlantable) tileEntity.getStackInSlot(slot).getItem()).getPlant(world, i, j, k);
		}
		else
		{
			block = Block.getBlockFromItem(tileEntity.getStackInSlot(slot).getItem());
		}
		
		if(world.getBlock(i, j, k).equals(Blocks.air) && block != null)
		{
			world.setBlock(i, j, k, block);
			
			tileEntity.getStackInSlot(slot).stackSize--;
			if(tileEntity.getStackInSlot(slot).stackSize == 0) tileEntity.setInventorySlotContents(slot, null);
		}
	}
	
	private int getI(int meta, int x)
	{
		switch(meta)
		{
		case 13: return x+1;
		case 12: return x-1;
		
		default: return x;
		}
	}
	
	private int getJ(int meta, int y)
	{
		switch(meta)
		{
		case 9: return 1+y;
		case 8: return y-1;
		
		default: return y;
		}
	}
	
	private int getK(int meta, int z)
	{
		switch(meta)
		{
		case 11: return z+1;
		case 10: return z-1;
		
		default: return z;
		}
	}
	
	private int getFirstSlot(TileEntityPlacer tileEntityPlacer, World world, int x, int y, int z) 
	{
		int slot = -1;
		for(int i = 0; i < 9; i++)
		{
			if(tileEntityPlacer.getStackInSlot(i) != null && tileEntityPlacer.getStackInSlot(i).stackSize != 0)
			{
				if(Block.getBlockFromItem(tileEntityPlacer.getStackInSlot(i).getItem()) != null)
				{
					return i;
				}
				if(tileEntityPlacer.getStackInSlot(i).getItem() instanceof IPlantable && world.getBlock(x, y-1, z).equals(Blocks.farmland))
				{
					return i;
				}
			}
		}
		return slot;
	}

	private void setDefaultDirection(World world, int x, int y, int z, EntityLivingBase livingBase)
	{
		int l = BlockPistonBase.determineOrientation(world, x, y, z, livingBase);
        world.setBlockMetadataWithNotify(x, y, z, l, 2);
	}
}
