package com.ahmedhnewa.data.product.category

interface ProductCategoryDataSource {
    suspend fun getAllCategories(): List<ProductCategory>
    suspend fun getCategoryById(id: String): ProductCategory?
    suspend fun addCategory(productCategory: ProductCategory): Boolean
    suspend fun updateCategory(updatedCategory: ProductCategory): Boolean
    suspend fun deleteCategory(id: String): Boolean
//    suspend fun deleteCategoryById(): Boolean
}