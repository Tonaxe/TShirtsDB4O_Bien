import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Entities.*;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

/**
 * @author Joan Anton Perez Branya
 * @since 19/02/2017
 *
 */

public class TShirtsDB4O {
	public static ArrayList<Order> orders;
	static ObjectContainer db;
	

	/**
	 * Implement TODO methods and run to test
	 * 
	 * @param args
	 *            no args
	 * @throws IOException
	 *             in order to read files
	 * @throws ParseException
	 *             in order to parse data formats
	 */
	public static void main(String[] args) throws IOException, ParseException {
		TShirtsDB4O TSM = new TShirtsDB4O();
		FileAccessor fa = new FileAccessor();
		fa.readArticlesFile("articles.csv");
		fa.readCreditCardsFile("creditCards.csv");
		fa.readCustomersFile("customers.csv");
		fa.readOrdersFile("orders.csv");
		fa.readOrderDetailsFile("orderDetails.csv");
		orders = fa.orders;
		try {

			File file = new File("orders.db");
			String fullPath = file.getAbsolutePath();
			db = Db4o.openFile(fullPath);

			TSM.addOrders();
			TSM.listOrders();
			TSM.listArticles();
			TSM.addArticle(7, "CALCETINES EJECUTIVOS 'JACKSON 3PK'", "gris", "45", 18.00);
			TSM.updatePriceArticle(7, 12.00);
			TSM.llistaArticlesByName("CALCETINES EJECUTIVOS 'JACKSON 3PK'");
			TSM.deletingArticlesByName("POLO BÁSICO 'MANIA'");
			TSM.deleteArticleById(7);
			TSM.listArticles();
			TSM.listCustomers();
			TSM.changeCreditCardToCustomer(1);
			TSM.listCustomers();
			TSM.llistaCustomerByName("Laura");
			TSM.showOrdersByCustomerName("Laura");
			TSM.showCreditCardByCustomerName("Laura");
			TSM.deleteCustomerbyId(2);
			TSM.retrieveOrderContentById_Order(2);
			TSM.deleteOrderContentById_Order(2);
			TSM.retrieveOrderContentById_Order(2);
			TSM.listCustomers();
			TSM.clearDatabase();
			TSM.listOrders();

		} finally {
			// close database
			db.close();
		}
	}

	/**
	 * Select Customer using customer id and next generate a new credit card and
	 * update customer using the new credit card
	 *
	 * @param i
	 *            idCustomer
	 */
	public void changeCreditCardToCustomer(int i) {
		System.out.println("Poner una nueva tarjeta de credito a un cliente");
		int[] creditCardNumbers = new int[16];
		for (int j = 0; j < 16; j++) {
			creditCardNumbers[j] = new Random().nextInt(10);
		}
		String creditCardNumber = "";
		for (int num : creditCardNumbers) {
			creditCardNumber += num;
		}
		CreditCard creditCard = new CreditCard(creditCardNumber, String.valueOf(creditCardNumbers[2])
				+ creditCardNumbers[5] + creditCardNumbers[12], new Random().nextInt(12) + 1,
				new Random().nextInt(10) + 20);
		db.store(creditCard);
		ObjectSet<Customer> result = db.queryByExample(new Customer(i, null, null, null, null, null));
		if (!result.isEmpty()) {
			result.get(0).setCreditCard(creditCard);
		}
	}

	/**
	 * Select Article using id and next update price
	 * 
	 * @param id
	 *            article
	 * @param newPrice
	 */
	public void updatePriceArticle(int id, double newPrice) {
		System.out.println("Actualizar precio de articulo");
		Article article = new Article();
		article.setIdArticle(id);
		ObjectSet<Article> result = db.queryByExample(article);
		if (!result.isEmpty()) {
			result.get(0).setRecommendedPrice((float) newPrice);
		}
	}

	/**
	 * Add a new article into database
	 * 
	 * @param i
	 *            article id
	 * @param string
	 *            article name
	 * @param string2
	 *            article colour
	 * @param string3
	 *            article size
	 * @param d
	 *            article price
	 */
	public void addArticle(int i, String string, String string2, String string3, double d) {
		System.out.println("Añadir articulo");
		Article article = new Article(i, string, string2, string3, (float) d);
		db.store(article);
		System.out.println(article.toString());
	}

	/**
	 * Delete an article using idArticle
	 * 
	 * @param i
	 *            idArticle
	 */
	public void deleteArticleById(int i) {
		System.out.println("Borrar articulo por ID");
		Article article = new Article();
		article.setIdArticle(i);
		ObjectSet<Article> result = db.queryByExample(article);
		if (!result.isEmpty()) {
			db.delete(result);
		}
	}

	/**
	 * Delete Order and its orderdetails using idOrder
	 * 
	 * @param i
	 *            idOrder
	 */
	public void deleteOrderContentById_Order(int i) {
		System.out.println("Borrar contenido de orden por ID de orden");
		Order order = new Order();
		order.setIdOrder(i);
		ObjectSet<Order> result = db.queryByExample(order);
		if (!result.isEmpty()) {
			result.get(0).setDetails(null);
		}
	}

	/**
	 * Select Order using his id and order details
	 *
	 * @param i
	 *            idOrder
	 */
	public void retrieveOrderContentById_Order(int i) {
		Order order = new Order();
		order.setIdOrder(i);
		ObjectSet<Order> result = db.queryByExample(order);

		if (!result.isEmpty()) {
			Order foundOrder = result.get(0);

			System.out.println("Contenido de la orden con ID " + i + ":");
			System.out.println(foundOrder.toString());

			for (OrderDetail detail : foundOrder.getDetails()) {
				System.out.println(detail.toString());
			}
		} else {
			System.out.println("No se encontro ninguna orden con el ID especificado.");
		}
	}


	/**
	 * Delete Customer using idCustomer
	 * 
	 * @param i
	 *            idCustomer
	 */
	public void deleteCustomerbyId(int i) {
		System.out.println("Borrar cliente por ID");
		ObjectSet<Customer> result = db.queryByExample(new Customer(i, null, null, null, null, null));
		if (!result.isEmpty()) {
			db.delete(result);
		}
	}

	/**
	 * Select Customer using customer name and next select the credit card
	 * values
	 * 
	 * @param string
	 *            customer name
	 */
	public void showCreditCardByCustomerName(String string) {
		List<Customer> customers = db.query(new Predicate<Customer>() {
			public boolean match(Customer customer) {
				return customer.getName().compareTo(string) == 0;
			}
		});
		System.out.println("Mostrar tarjeta de credito por nombre del cliente");
		for (Customer c : customers) {
			System.out.println(c.getCreditCard());
		}
	}

	/**
	 * Method to list Oders and orderdetails from the database using the
	 * customer name
	 */
	public void showOrdersByCustomerName(String customerName) {
		List<Order> orders = db.query(new Predicate<Order>() {
			public boolean match(Order order) {
				return order.getCustomer().getName().compareTo(customerName) == 0;
			}
		});
		System.out.println("Mostrar ordenes por nombre del cliente");
		for (Order o : orders) {
			System.out.println(o.toString());
		}
	}

	/** delete all objects from the whole database */
	public void clearDatabase() {
		System.out.println("Borrar toda la base de datos");
		db.queryByExample(new Article()).forEach(db::delete);
		db.queryByExample(new CreditCard()).forEach(db::delete);
		db.queryByExample(new Customer()).forEach(db::delete);
		db.queryByExample(new OrderDetail()).forEach(db::delete);
		db.queryByExample(new Order()).forEach(db::delete);
	}

	/**
	 * Delete Article using article name
	 * 
	 * @param string
	 *            Article name
	 */
	public void deletingArticlesByName(String string) {
		System.out.println("Borrar todos los artículos por nombre");
		Article article = new Article();
		article.setName(string);
		ObjectSet<Article> result = db.queryByExample(article);
		while (result.hasNext()) {
			db.delete(result);
		}
	}

	/** Method to list Articles from the database using their name */
	public void llistaArticlesByName(String articleName) {
		List<Article> articles = db.query(new Predicate<Article>() {
			public boolean match(Article article) {
				return article.getName().compareTo(articleName) == 0;
			}
		});

		System.out.println("Listar todos los articulos por nombre");
		for (Article a : articles) {
			System.out.println(a.toString());
		}
	}

	/** Method to list Customers from the database using their name */
	public void llistaCustomerByName(String customerName) {
		List<Customer> customers = db.query(new Predicate<Customer>() {
			public boolean match(Customer customer) {
				return customer.getName().compareTo(customerName) == 0;
			}
		});

		System.out.println("Listar todos los clientes por nombre");
		for (Customer c : customers) {
			System.out.println(c.toString());
		}
	}

	/** Method to list all Customers from the database */
	public void listCustomers() {
		System.out.println("Listar todos los clientes");
		ObjectSet<Customer> result = db.queryByExample(new Customer());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	/** Method to list all Articles from the database */
	public void listArticles() {
		System.out.println("Listar todos los articulos");
		ObjectSet<Article> result = db.queryByExample(new Article());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	/** Method to add all orders from ArrayList and store them into database */
	public void addOrders() {
		System.out.println("Añadir ordenes");
		for (Order o : orders) {
			db.store(o);
			System.out.println(o.toString());
		}
	}

	/** Method to list all Orders from the database */
	public void listOrders() {
		System.out.println("Listar todas las ordenes");
		ObjectSet<Order> result = db.queryByExample(new Order());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
}
