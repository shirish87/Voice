package voice.bookOverview.editBookCategory

import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import voice.bookOverview.bottomSheet.BottomSheetItem
import voice.bookOverview.bottomSheet.BottomSheetItemViewModel
import voice.bookOverview.di.BookOverviewScope
import voice.bookOverview.overview.BookOverviewCategory
import voice.bookOverview.overview.category
import voice.data.Book
import voice.data.repo.BookRepository
import javax.inject.Inject

@BookOverviewScope
@ContributesMultibinding(BookOverviewScope::class)
class EditBookCategoryViewModel
@Inject
constructor(
  private val repo: BookRepository,
) : BottomSheetItemViewModel {

  override val menuOrder: Int
    get() = BottomSheetItem.BookCategoryMarkAsNotStarted.ordinal

  private val menuItems = listOf(
    BottomSheetItem.BookCategoryMarkAsCurrent,
    BottomSheetItem.BookCategoryMarkAsNotStarted,
    BottomSheetItem.BookCategoryMarkAsCompleted,
  )

  override suspend fun items(bookId: Book.Id): List<BottomSheetItem> = withContext(Dispatchers.IO) {
    val book = repo.get(bookId) ?: return@withContext emptyList()
    val bookOverviewCategory = book.category

    menuItems.filter { bottomSheetItem ->
      when (bottomSheetItem) {
        BottomSheetItem.BookCategoryMarkAsCurrent -> (bookOverviewCategory != BookOverviewCategory.CURRENT)
        BottomSheetItem.BookCategoryMarkAsNotStarted -> (bookOverviewCategory != BookOverviewCategory.NOT_STARTED)
        BottomSheetItem.BookCategoryMarkAsCompleted -> (bookOverviewCategory != BookOverviewCategory.FINISHED)
        else -> false
      }
    }
  }

  override suspend fun onItemClicked(bookId: Book.Id, item: BottomSheetItem) = withContext(Dispatchers.IO) {
    if (!menuItems.contains(item)) return@withContext false
    val book = repo.get(bookId) ?: return@withContext false

    val (currentChapter, positionInChapter) = when (item) {
      BottomSheetItem.BookCategoryMarkAsCurrent -> {
        Pair(book.chapters.first().id, 1L)
      }
      BottomSheetItem.BookCategoryMarkAsNotStarted -> {
        Pair(book.chapters.first().id, 0L)
      }
      BottomSheetItem.BookCategoryMarkAsCompleted -> {
        val lastChapter = book.chapters.last()
        Pair(lastChapter.id, lastChapter.duration)
      }
      else -> return@withContext false
    }

    repo.updateBook(book.id) {
      it.copy(
        currentChapter = currentChapter,
        positionInChapter = positionInChapter,
      )
    }

    true
  }
}
