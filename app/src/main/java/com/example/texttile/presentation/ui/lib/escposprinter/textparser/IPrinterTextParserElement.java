package com.example.texttile.presentation.ui.lib.escposprinter.textparser;

import com.example.texttile.presentation.ui.lib.escposprinter.EscPosPrinterCommands;
import com.example.texttile.presentation.ui.lib.escposprinter.exceptions.EscPosConnectionException;
import com.example.texttile.presentation.ui.lib.escposprinter.exceptions.EscPosEncodingException;

public interface IPrinterTextParserElement {
    int length() throws EscPosEncodingException;
    IPrinterTextParserElement print(EscPosPrinterCommands printerSocket) throws EscPosEncodingException, EscPosConnectionException;
}
