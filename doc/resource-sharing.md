# Resource sharing

<!-- md2toc -l 2 resource-sharing.md -->
* [Scenario](#scenario)
* [Problems](#problems)
* [Protocols](#protocols)
* [Implementation](#implementation)
* [Notes](#notes)

## Scenario

Consider a patron of a library who wants to read a book that his library does not have. The library is part of a borrowing consortium with several other libraries. "Resource sharing" is the process of fulfilling the patron's request from one of the other libraries.

## Problems

The first problem is **discovery**. We must determine:

1. Which of the other libraries has the book.
2. Which of these have it on the shelves (not on loan).
3. Which of these is prepared to lend it.

#1 is standard MasterKey-like discovery. #2 is more complex, as different libraries represent in the in-stock status of their items in different ways. There may be a role for the CF here. #3 depends on potentially fairly complex library-specific rule-sets depending on the material-type, the lending history, and other factors.

Potentially many libraries provide these filters, so that several different sources could provide the library. To avoid over-stressing one library with a constant stream of requests (e.g. we always ask the Aardark Library, never the Zygote Library), we need to do **load-balancing** of requests. A simple approach just randomises requests. A more sophisticated approach counts loan requests sent to each library and tries to keep them in balance. "Balance" means in proportion to some factor such as library budget, patron-count, inventory size, or a combination.

Once a suitable lending library has been identified, the problem becomes **requesting**. First, we need to generate the data that constitutes the request: identification of the book and user, and perhaps user-provided data such as the reason for the loan.

Then we need to send the request. There are various mechanisms for doing this, but simply sending an email to the relevant librarian covers 90% of the requirements. Implementing NCIP will cover most of the rest. There may be additional mechanisms, but they are much less important.

Typically, a temporary local record is created for the item that is loaned, so that this library can take responsibility for it.

Usually, an item is eventually returned to its original library (at which point the temporary records at both ends should be removed). But since there is a cost associated with returning, sometimes the item is simply left at the library that borrowed it. This results in a _floating collection_.

## Protocols

An ILS, or a discovery layer like EDS or Summon, might have resource-sharing built in; or it may communicate what's desired using **OpenURL**. (This is how Borrow Direct does things.)

**Z39.50** is still standard for discovery, and can also provide circulation information.

**NCIP** supports remote holds on the lending library, and can also create a temporary patron record on the remove system. NCIP2 exists, and is supposed to be easier to implement; but support will not be impressive at this stage.

**ISO ILL** is a rather different alternative to NCIP, based more on older procedures with stamps and forms.

And finally, we can always **screen-scrape** to get at information that is not exposed, potentially including circulation status.

## Implementation

Much of what we need for this already exists in and around MasterKey.

We can use SP/Pazpar2 to search the member libraries' Z39.50 catalogues for the desired item. Many libraries will return OPAC records that give (among other things) circulation status. For those that lack this, we can use RTAC-like connectors to scrape this status off their web-sites.

The loan-rules part, and the UI for maintaining them, does not yet exist -- but we will be building something similar for FOLIO's circulation module, so it should be possible to either use that or adapt it. We will also need an engine for evaluating the rules to determine a set of candidate libraries that are able and willing to lend the item.

Load-balancing in version 1 can just be asking a librarian to choose on the candidate libraries.

The request can be encoded as a metadata object. The ISO ILL standard contains a format that may be suitable; or we may need to extend this or roll our own.

Sending email is not rocket science, but [fraught with annoying wrinkles](https://blog.codinghorror.com/so-youd-like-to-send-some-email-through-code/). I don't think we have NCIP code at this point, but there may be an open-source implementation that we can use.

## Notes

It's possible to detect in the local library ILS's web logs that requests have been sent.

There is a price for a loan-transaction (and of course to returning the loaned item). It may be cheaper to simply buy the item. A clever system can make this decision automatically. It may even be worth buying a book for a price higher than the loan cost, optimising for speed. In this case, integration with the acquisition system will be desirable.

