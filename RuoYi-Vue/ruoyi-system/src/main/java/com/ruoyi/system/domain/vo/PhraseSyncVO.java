package com.ruoyi.system.domain.vo;

import java.util.List;
import com.ruoyi.system.domain.SysPhraseBook;
import com.ruoyi.system.domain.SysPhraseItem;
import com.ruoyi.system.domain.SysPhraseDetail;

/**
 * 规范用语同步VO
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class PhraseSyncVO
{
    /** 书本列表 */
    private List<SysPhraseBook> books;

    /** 项列表 */
    private List<SysPhraseItem> items;

    /** 明细列表 */
    private List<SysPhraseDetail> details;

    /** 书本版本 */
    private Integer bookVersion;

    /** 项版本 */
    private Integer itemVersion;

    /** 明细版本 */
    private Integer detailVersion;

    /** 同步时间 */
    private Long syncTime;

    public List<SysPhraseBook> getBooks()
    {
        return books;
    }

    public void setBooks(List<SysPhraseBook> books)
    {
        this.books = books;
    }

    public List<SysPhraseItem> getItems()
    {
        return items;
    }

    public void setItems(List<SysPhraseItem> items)
    {
        this.items = items;
    }

    public List<SysPhraseDetail> getDetails()
    {
        return details;
    }

    public void setDetails(List<SysPhraseDetail> details)
    {
        this.details = details;
    }

    public Integer getBookVersion()
    {
        return bookVersion;
    }

    public void setBookVersion(Integer bookVersion)
    {
        this.bookVersion = bookVersion;
    }

    public Integer getItemVersion()
    {
        return itemVersion;
    }

    public void setItemVersion(Integer itemVersion)
    {
        this.itemVersion = itemVersion;
    }

    public Integer getDetailVersion()
    {
        return detailVersion;
    }

    public void setDetailVersion(Integer detailVersion)
    {
        this.detailVersion = detailVersion;
    }

    public Long getSyncTime()
    {
        return syncTime;
    }

    public void setSyncTime(Long syncTime)
    {
        this.syncTime = syncTime;
    }
}
