package com.example.texttile.presentation.ui.lib.escposprinter.barcode;

import com.example.texttile.presentation.ui.lib.escposprinter.EscPosPrinterCommands;
import com.example.texttile.presentation.ui.lib.escposprinter.EscPosPrinterSize;
import com.example.texttile.presentation.ui.lib.escposprinter.exceptions.EscPosBarcodeException;

public class BarcodeEAN13 extends BarcodeNumber {

    public BarcodeEAN13(EscPosPrinterSize printerSize, String code, float widthMM, float heightMM, int textPosition) throws EscPosBarcodeException {
        super(printerSize, EscPosPrinterCommands.BARCODE_TYPE_EAN13, code, widthMM, heightMM, textPosition);
    }

    @Override
    public int getCodeLength() {
        return 13;
    }
}
