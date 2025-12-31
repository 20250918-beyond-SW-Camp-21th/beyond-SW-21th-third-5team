import { defineComponent, h } from 'vue';

function createUiComponent(tag: string, baseClass = '') {
  return defineComponent({
    name: tag,
    setup(_, { slots, attrs }) {
      return () => h(tag, { class: baseClass, ...attrs }, slots.default?.());
    },
  });
}

export const Button = createUiComponent('button');
export const Badge = createUiComponent('span');
export const Input = createUiComponent('input');
export const Textarea = createUiComponent('textarea');
export const Switch = createUiComponent('button');
export const Slider = createUiComponent('div');
export const Checkbox = createUiComponent('input');
export const Select = createUiComponent('select');
export const Avatar = createUiComponent('div');
export const Calendar = createUiComponent('div');
export const Card = createUiComponent('div');
export const CardHeader = createUiComponent('div');
export const CardContent = createUiComponent('div');
export const CardTitle = createUiComponent('h3');
export const CardDescription = createUiComponent('p');
export const Carousel = createUiComponent('div');
export const CarouselItem = createUiComponent('div');
export const Table = createUiComponent('table');
export const TableRow = createUiComponent('tr');
export const TableCell = createUiComponent('td');
export const Accordion = createUiComponent('div');
export const AccordionItem = createUiComponent('div');
export const AccordionTrigger = createUiComponent('button');
export const AccordionContent = createUiComponent('div');
export const Tabs = createUiComponent('div');
export const TabsList = createUiComponent('div');
export const TabsTrigger = createUiComponent('button');
export const TabsContent = createUiComponent('div');
export const NavigationMenu = createUiComponent('nav');
export const NavigationMenuList = createUiComponent('div');
export const NavigationMenuItem = createUiComponent('div');
export const NavigationMenuContent = createUiComponent('div');
export const Popover = createUiComponent('div');
export const PopoverTrigger = createUiComponent('button');
export const PopoverContent = createUiComponent('div');
export const Tooltip = createUiComponent('div');
export const TooltipTrigger = createUiComponent('button');
export const TooltipContent = createUiComponent('div');
export const DropdownMenu = createUiComponent('div');
export const DropdownMenuTrigger = createUiComponent('button');
export const DropdownMenuContent = createUiComponent('div');
export const DropdownMenuItem = createUiComponent('div');
export const ContextMenu = createUiComponent('div');
export const ContextMenuTrigger = createUiComponent('div');
export const ContextMenuContent = createUiComponent('div');
export const HoverCard = createUiComponent('div');
export const HoverCardTrigger = createUiComponent('div');
export const HoverCardContent = createUiComponent('div');
export const Drawer = createUiComponent('div');
export const DrawerTrigger = createUiComponent('button');
export const DrawerContent = createUiComponent('div');
export const Sheet = createUiComponent('div');
export const SheetTrigger = createUiComponent('button');
export const SheetContent = createUiComponent('div');
export const Alert = createUiComponent('div');
export const AlertDialog = createUiComponent('div');
export const AlertDialogTrigger = createUiComponent('button');
export const AlertDialogContent = createUiComponent('div');
export const Progress = createUiComponent('div');
export const Separator = createUiComponent('hr');
export const Breadcrumb = createUiComponent('nav');
export const Pagination = createUiComponent('div');
export const Skeleton = createUiComponent('div');
export const Toggle = createUiComponent('button');
export const ToggleGroup = createUiComponent('div');
export const Sidebar = createUiComponent('aside');
export const ResizablePanel = createUiComponent('div');
export const ResizableHandle = createUiComponent('div');
export const Menubar = createUiComponent('nav');
export const Command = createUiComponent('div');
export const RadioGroup = createUiComponent('div');
export const RadioGroupItem = createUiComponent('div');
export const Sonner = createUiComponent('div');
