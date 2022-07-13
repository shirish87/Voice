package voice.bookOverview.editTitle

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import voice.bookOverview.bottomSheet.BottomSheetItem
import voice.bookOverview.bottomSheet.BottomSheetItemViewModel
import voice.bookOverview.di.BookOverviewScope
import voice.data.Book
import voice.data.repo.BookRepository
import javax.inject.Inject

@BookOverviewScope
@ContributesMultibinding(BookOverviewScope::class)
class EditBookTitleViewModel
@Inject
constructor(
  private val repo: BookRepository,
) : BottomSheetItemViewModel {

  override val menuOrder: Int
    get() = BottomSheetItem.Title.ordinal

  private val scope = MainScope()

  private val _state = mutableStateOf<EditBookTitleState?>(null)
  internal val state: State<EditBookTitleState?> get() = _state

  override suspend fun items(bookId: Book.Id): List<BottomSheetItem> {
    return listOf(BottomSheetItem.Title)
  }

  override suspend fun onItemClicked(bookId: Book.Id, item: BottomSheetItem): Boolean {
    if (item != BottomSheetItem.Title) return false
    scope.launch {
      val book = repo.get(bookId) ?: return@launch
      _state.value = EditBookTitleState(
        title = book.content.name,
        bookId = bookId,
      )
    }

    return true
  }

  internal fun onDismissEditTitle() {
    _state.value = null
  }

  internal fun onUpdateEditTitle(title: String) {
    _state.value = _state.value?.copy(title = title)
  }

  internal fun onConfirmEditTitle() {
    val state = _state.value
    if (state != null) {
      check(state.confirmButtonEnabled)
      scope.launch {
        repo.updateBook(state.bookId) {
          it.copy(name = state.title.trim())
        }
      }
    }
    _state.value = null
  }
}
