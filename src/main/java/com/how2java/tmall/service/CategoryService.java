package com.how2java.tmall.service;

import com.how2java.tmall.dao.CategoryDAO;
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

@Service
public class CategoryService {
	@Autowired
	CategoryDAO categoryDAO;


	public Page4Navigator<Category> list(int start, int size, int navigatePages) {
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageRequest(start, size, sort);
		Page pageFromJPA = categoryDAO.findAll(pageable);

		return new Page4Navigator<>(pageFromJPA, navigatePages);
	}

	public List<Category> list() {
		Sort sort = new Sort(Sort.Direction.ASC, "id");
		List<Category> categories = categoryDAO.findAll(sort);

		List<Category> level1 = new ArrayList<>();
		// 找到所有的一级分类
		for (Category entity : categories) {
			if (entity.getParent_cid() == 0) {
				level1.add(entity);
			}
		}
		for (Category level1Menu : level1) {
			level1Menu.setChildren(getChildrens(level1Menu, categories));
		}
		return level1;

	}

	/**
	 * 递归查找所有的下级分类
	 * 在这一级别的分类中找下级分类
	 *
	 * @param root 当前级别的分类
	 * @param all  全部分类
	 * @return 下一级分类
	 */
	private List<Category> getChildrens(Category root, List<Category> all) {
		List<Category> children = new ArrayList<>();
		for (Category a : all) {
			if (a.getParent_cid() == (root.getId())) {
				children.add(a);
				a.setChildren(getChildrens(a, all));

			}
		}
		return children;
	}

	public void add(Category bean) {
		categoryDAO.save(bean);
	}

	public void delete(int id) {
		categoryDAO.delete(id);
	}

	public Category get(int id) {
		Category c = categoryDAO.findOne(id);
		return c;
	}

	public void update(Category bean) {
		categoryDAO.save(bean);
	}


	public void removeCategoryFromProduct(List<Category> cs) {
		for (Category category : cs) {
			removeCategoryFromProduct(category);
		}
	}

	public void removeCategoryFromProduct(Category category) {
			List<Product> products = category.getProducts();
			if (!(products.isEmpty())) {
				for (Product product : products) {
					product.setCategory(null);
				}

			}
			else {
				for (Category category1 : category.getChildren()) {
					 removeCategoryFromProduct(category1);
				}
			}


		List<List<Product>> productsByRow =category.getProductsByRow();
		if(null!=productsByRow) {
			for (List<Product> ps : productsByRow) {
				for (Product p: ps) {
					p.setCategory(null);
				}
			}
		}

	}



}

