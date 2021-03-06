package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ProductService  {
	
	@Autowired ProductDAO productDAO;
	@Autowired CategoryService categoryService;
	@Autowired ProductImageService productImageService;
	@Autowired OrderItemService orderItemService;
	@Autowired ReviewService reviewService;

	public void add(Product bean) {
		productDAO.save(bean);
	}

	public void delete(int id) {
		productDAO.delete(id);
	}

	public Product get(int id) {
		return productDAO.findOne(id);
	}

	public void update(Product bean) {
		productDAO.save(bean);
	}

	public Page4Navigator<Product> list(int cid, int start, int size,int navigatePages) {
    	Category category = categoryService.get(cid);
    	Sort sort = new Sort(Sort.Direction.DESC, "id");
    	Pageable pageable = new PageRequest(start, size, sort);    	
    	Page<Product> pageFromJPA =productDAO.findByCategory(category,pageable);
    	return new Page4Navigator<>(pageFromJPA,navigatePages);
	}

	public void fill(List<Category> categorys) {
		for (Category category : categorys) {
			fill(category);
		}
	}
	public void fill(Category category) {
		List<Product> products =  productDAO.findAll();
category.setProducts(getProducts(category, products));

		//List<Product> products = listByCategory(category);
		productImageService.setFirstProdutImages(products);
	}

	private List<Product> getProducts(Category root, List<Product> all) {
		List<Product> product = new ArrayList<>();
		CopyOnWriteArrayList<Category> categorys =new CopyOnWriteArrayList<>();
		for (Product a : all) {
			if (a.getCategory().getName().equals(root.getName())) {
				product.add(a);
			}
		}
		if(product.isEmpty()){
			root.setProducts(product);
			categorys=root.getChildren();
			for(Category categoryChildren:categorys){
				categoryChildren.setProducts(getProducts(categoryChildren,all));
			}
		}
		return product;
	}


	public void fillByRow(List<Category> categorys) {
		int productNumberEachRow = 8;
		CopyOnWriteArrayList<Product> products = new CopyOnWriteArrayList<>();

		for (Category category : categorys) {//???????????? ,????????????
			if(!category.getChildren().isEmpty()) {//children??????,
				for (Category children : category.getChildren()) {//children??????????????????          ?????? ??????
					products.clear();
					if (children.getChildren().isEmpty()) {//children???
						for (Product product1 : children.getProducts()) {
							products.add(product1);//???????????????????????????
						}
					} else {
						products= fillByRow2(children.getChildren());//???????????? ?????????
					}

					CopyOnWriteArrayList<CopyOnWriteArrayList<Product>> productsByRow =  new CopyOnWriteArrayList<>();
					for (int i = 0; i < products.size(); i+=productNumberEachRow) {
						int size = i+productNumberEachRow;
						size= size>products.size()?products.size():size;
					List<Product> productsOfEachRow =  products.subList(i, size);
						CopyOnWriteArrayList cal	=new CopyOnWriteArrayList(productsOfEachRow);
						productsByRow.add(cal);
					}

					children.setProductsByRow(productsByRow);
				}
			}else{
				for (Product product2 : category.getProducts()) {
					products.add(product2);
				}
			}
		}
	}

	public CopyOnWriteArrayList<Product> fillByRow2(CopyOnWriteArrayList<Category> categorys) {
		CopyOnWriteArrayList<Product> products2 = new CopyOnWriteArrayList<>();
		for (Category category : categorys) {   //??????
				for (Product product2 : category.getProducts()) {
					products2.add(product2);
				}

		}
		return products2;
	}


	public List<Product> listByCategory(Category category){

		return productDAO.findByCategoryOrderById(category);
	}


	public void setSaleAndReviewNumber(List<Product> products) {
		for (Product product : products)
			setSaleAndReviewNumber(product);
	}
	public void setSaleAndReviewNumber(Product product) {
		int saleCount = orderItemService.getSaleCount(product);
		product.setSaleCount(saleCount);

		int reviewCount = reviewService.getCount(product);
		product.setReviewCount(reviewCount);

	}

	public List<Product> search(String keyword, int start, int size) {
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageRequest(start, size, sort);
		List<Product> products =productDAO.findByNameLike("%"+keyword+"%",pageable);
		return products;
	}
}
