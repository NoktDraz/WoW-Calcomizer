package main.enums;

import main.model.ItemTooltip;

public enum TooltipPreset {
    TALENT_RANK,
    NOTE_DESCRIPTION,
    ERROR_FINALIZING;

    private ItemTooltip tooltip;

    public ItemTooltip getTooltip(TooltipPreset preset) {
        this.tooltip = new ItemTooltip();

        switch (preset) {
            case TALENT_RANK -> this.talentRank();
            case NOTE_DESCRIPTION -> this.noteDescription();
            case ERROR_FINALIZING -> this.errorFinalizing();
        }


        return this.tooltip;
    }

    private void talentRank() {
    }
    private void noteDescription() {
    }
    private void errorFinalizing() {
    }
}
