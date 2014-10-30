package combinatorics.optimization;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import combinatorics.optimization.model.Item;

public class BoundedKnapsack {
	protected List<Item> itemList = new ArrayList<Item>();
	protected int maxWeight = 0;
	protected int solutionWeight = 0;
	protected int profit = 0;
	protected boolean calculated = false;
	
	public static void main(String[] args) {
		BoundedKnapsack zok = new BoundedKnapsack(35); // 400 dkg = 400 dag = 4 kg
		 
        // making the list of items that you want to bring
        zok.add("1", 4, 7);
        zok.add("2", 7, 10);
        zok.add("3", 11, 15);
        zok.add("4", 12, 20);
        zok.add("5", 16, 27);
        zok.add("6", 20, 34);
 
        // calculate the solution:
        List<Item> itemList = zok.calcSolution();
 
        // write out the solution in the standard output
        if (zok.isCalculated()) {
            NumberFormat nf  = NumberFormat.getInstance();
 
            System.out.println(
                "Maximal weight           = " +
                nf.format(zok.getMaxWeight()) + " kg"
            );
            System.out.println(
                "Total weight of solution = " +
                nf.format(zok.getSolutionWeight()) + " kg"
            );
            System.out.println(
                "Total value              = " +
                zok.getProfit()
            );
            System.out.println();
            System.out.format("%1$-5s %2$3s %3$5s \n", "item", "kg  ", "cost");
            for (Item item : itemList) {
                if (item.getInKnapsack() == 1) {
                    System.out.format(
                        "%1$-5s %2$-3s %3$5s\n",
                        item.getName(),
                        item.getWeight(),
                        item.getValue());
                }
            }
        } else {
            System.out.println(
                "The problem is not solved. " +
                "Maybe you gave wrong data."
            );
        }
	}

	public BoundedKnapsack() {
	}

	public BoundedKnapsack(int _maxWeight) {
		setMaxWeight(_maxWeight);
	}

	public BoundedKnapsack(List<Item> _itemList) {
		setItemList(_itemList);
	}

	public BoundedKnapsack(List<Item> _itemList, int _maxWeight) {
		setItemList(_itemList);
		setMaxWeight(_maxWeight);
	}

	// calculte the solution of 0-1 knapsack problem with dynamic method:
	public List<Item> calcSolution() {
		int n = itemList.size();

		setInitialStateForCalculation();
		if (n > 0 && maxWeight > 0) {
			List<List<Integer>> c = new ArrayList<List<Integer>>();
			List<Integer> curr = new ArrayList<Integer>();

			c.add(curr);
			for (int j = 0; j <= maxWeight; j++)
				curr.add(0);
			for (int i = 1; i <= n; i++) {
				List<Integer> prev = curr;
				c.add(curr = new ArrayList<Integer>());
				for (int j = 0; j <= maxWeight; j++) {
					if (j > 0) {
						int wH = itemList.get(i - 1).getWeight();
						curr.add((wH > j) ? prev.get(j) : Math.max(
								prev.get(j),
								itemList.get(i - 1).getValue()
										+ prev.get(j - wH)));
					} else {
						curr.add(0);
					}
				} // for (j...)
			} // for (i...)
			profit = curr.get(maxWeight);

			for (int i = n, j = maxWeight; i > 0 && j >= 0; i--) {
				int tempI = c.get(i).get(j);
				int tempI_1 = c.get(i - 1).get(j);
				if ((i == 0 && tempI > 0) || (i > 0 && tempI != tempI_1)) {
					Item iH = itemList.get(i - 1);
					int wH = iH.getWeight();
					iH.setInKnapsack(1);
					j -= wH;
					solutionWeight += wH;
				}
			} // for()
			calculated = true;
		} // if()
		return itemList;
	}

	// add an item to the item list
	public void add(String name, int weight, int value) {
		if (name.equals(""))
			name = "" + (itemList.size() + 1);
		itemList.add(new Item(name, value, weight));
		setInitialStateForCalculation();
	}

	// add an item to the item list
	public void add(int weight, int value) {
		add("", weight, value); // the name will be "itemList.size() + 1"!
	}

	// remove an item from the item list
	public void remove(String name) {
		for (Iterator<Item> it = itemList.iterator(); it.hasNext();) {
			if (name.equals(it.next().getName())) {
				it.remove();
			}
		}
		setInitialStateForCalculation();
	}

	// remove all items from the item list
	public void removeAllItems() {
		itemList.clear();
		setInitialStateForCalculation();
	}

	public int getProfit() {
		if (!calculated)
			calcSolution();
		return profit;
	}

	public int getSolutionWeight() {
		return solutionWeight;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int _maxWeight) {
		maxWeight = Math.max(_maxWeight, 0);
	}

	public void setItemList(List<Item> _itemList) {
		if (_itemList != null) {
			itemList = _itemList;
			for (Item item : _itemList) {
				item.checkMembers();
			}
		}
	}

	// set the member with name "inKnapsack" by all items:
	private void setInKnapsackByAll(int inKnapsack) {
		for (Item item : itemList)
			if (inKnapsack > 0)
				item.setInKnapsack(1);
			else
				item.setInKnapsack(0);
	}

	// set the data members of class in the state of starting the calculation:
	protected void setInitialStateForCalculation() {
		setInKnapsackByAll(0);
		calculated = false;
		profit = 0;
		solutionWeight = 0;
	}
}
