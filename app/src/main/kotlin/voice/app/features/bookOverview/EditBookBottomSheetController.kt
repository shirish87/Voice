package voice.app.features.bookOverview

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.anvil.annotations.ContributesTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import voice.app.databinding.BookMoreBottomSheetBinding
import voice.app.features.bookmarks.BookmarkController
import voice.app.misc.RouterProvider
import voice.app.misc.conductor.asTransaction
import voice.bookOverview.BookOverviewCategory
import voice.bookOverview.category
import voice.common.conductor.DialogController
import voice.core.AppScope
import voice.core.rootComponentAs
import voice.data.Book
import voice.data.getBookId
import voice.data.putBookId
import voice.data.repo.BookRepository
import javax.inject.Inject

class EditBookBottomSheetController(args: Bundle) : DialogController(args) {

  @Inject
  lateinit var bookRepo: BookRepository

  private val bookId = args.getBookId(NI_BOOK)!!

  init {
    rootComponentAs<Component>().inject(this)
  }

  override fun onCreateDialog(savedViewState: Bundle?): Dialog {
    val book = runBlocking { bookRepo.get(bookId) }
      ?: error("Cannot instantiate ${javaClass.name} without a current book")

    val binding = BookMoreBottomSheetBinding.inflate(activity!!.layoutInflater)
    binding.title.setOnClickListener {
      val router = (activity as RouterProvider).provideRouter()
      EditBookTitleDialogController(bookId).showDialog(router)
      dismissDialog()
    }
    binding.internetCover.setOnClickListener {
      callback().onInternetCoverRequested(bookId)
      dismissDialog()
    }
    binding.fileCover.setOnClickListener {
      callback().onFileCoverRequested(bookId)
      dismissDialog()
    }
    binding.bookmark.setOnClickListener {
      val router = (activity as RouterProvider).provideRouter()
      val controller = BookmarkController(bookId)
      router.pushController(controller.asTransaction())

      dismissDialog()
    }
    binding.markAsNotStarted.apply {
      visibility = if (book.category == BookOverviewCategory.NOT_STARTED) View.GONE else View.VISIBLE

      setOnClickListener {
        val firstChapter = book.chapters.first()

        lifecycleScope.launch {
          bookRepo.updateBook(bookId) {
            it.copy(
              currentChapter = firstChapter.id,
              positionInChapter = 0,
            )
          }

          withContext(Dispatchers.Main) {
            dismissDialog()
          }
        }
      }
    }
    binding.markAsCurrent.apply {
      visibility = if (book.category == BookOverviewCategory.CURRENT) View.GONE else View.VISIBLE

      setOnClickListener {
        val firstChapter = book.chapters.first()

        lifecycleScope.launch {
          bookRepo.updateBook(bookId) {
            it.copy(
              currentChapter = firstChapter.id,
              positionInChapter = 1,
            )
          }

          withContext(Dispatchers.Main) {
            dismissDialog()
          }
        }
      }
    }
    binding.markAsComplete.apply {
      visibility = if (book.category == BookOverviewCategory.FINISHED) View.GONE else View.VISIBLE

      setOnClickListener {
        val lastChapter = book.chapters.last()

        lifecycleScope.launch {
          bookRepo.updateBook(bookId) {
            it.copy(
              currentChapter = lastChapter.id,
              positionInChapter = lastChapter.duration,
            )
          }

          withContext(Dispatchers.Main) {
            dismissDialog()
          }
        }
      }
    }

    return BottomSheetDialog(activity!!).apply {
      setContentView(binding.root)
    }
  }

  private fun callback() = targetController as Callback

  companion object {
    private const val NI_BOOK = "ni#book"
    operator fun <T> invoke(
      target: T,
      id: Book.Id
    ): EditBookBottomSheetController where T : Controller, T : Callback {
      val args = Bundle().apply {
        putBookId(NI_BOOK, id)
      }
      return EditBookBottomSheetController(args).apply {
        targetController = target
      }
    }
  }

  interface Callback {
    fun onInternetCoverRequested(book: Book.Id)
    fun onFileCoverRequested(book: Book.Id)
  }

  @ContributesTo(AppScope::class)
  interface Component {
    fun inject(target: EditBookBottomSheetController)
  }
}
